package com.carrafasoft.bsuldo.api.exceptionhandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.carrafasoft.bsuldo.api.enums.ProblemTypeEnum;
import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.exception.entidadeException.EntidadeEmUsoException;
import com.carrafasoft.bsuldo.exceptions.AcompanhamentoEstrategicoExistenteException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BsuldoExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String CORPO_DA_REQUISICAO_INVALIDO = "O Corpo da requisição está inválido. Verifique erro de sintaxe.";
	private static final String PROPRIEDADE_COM_VALOR_INVALIDO_ERRO = "A propriedade '%s' recebeu o valor '%s' que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %S.";
	private static final String PROPRIEDADE_INEXISTENTE = "A propriedade '%s' não existe. Corrija ou remova essa propriedade e tente novamente.";
	private static final String PARAMETRO_INVALIDO = "O parâmetro de URL '%s' recebeu o valor '%s', que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.";
	private static final String RECURSO_NAO_ENCONTRADO = "O recurso %s, que você tentou acessar, é inexistente.";
	private static final String ERRO_INTERNO_500 = "Ocorreu um erro interno inesperado no sistema. "
			+ "Tente novamente e se o problema persistir, entre em contato "
			+ "com o administrador do sistema.";

	private static final String DADOS_OU_CAMPOS_INVALIDOS = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";

	@Autowired
	private MessageSource messageSource;

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		Throwable rootCause = ExceptionUtils.getRootCause(ex);

		if(rootCause instanceof InvalidFormatException) {
			return handleInvalidFormatException((InvalidFormatException) rootCause, headers, status, request);
		} else if (rootCause instanceof PropertyBindingException) {
			return handlePropertyBindingException((PropertyBindingException) rootCause, headers, status, request);
		}

		ProblemTypeEnum problemType = ProblemTypeEnum.MENSAGEM_INCOMPREENSIVEL;
		String detail = CORPO_DA_REQUISICAO_INVALIDO;

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();

		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers, HttpStatus status, WebRequest request) {

		ProblemTypeEnum problemType = ProblemTypeEnum.DADOS_INVALIDOS;
		String detail = DADOS_OU_CAMPOS_INVALIDOS;

		BindingResult bindingResult = ex.getBindingResult();

		List<Problem.Field> problemFields = bindingResult.getFieldErrors().stream()
				.map(fieldError -> Problem.Field.builder()
						.name(fieldError.getField())
						.userMessage(fieldError.getDefaultMessage())
						.build())
				.collect(Collectors.toList());

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.fields(problemFields)
				.build();

		return handleExceptionInternal(ex, problem, headers, status, request);
	}
	
	@ExceptionHandler({ EmptyResultDataAccessException.class })
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
		
		String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		
		
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler({ DataIntegrityViolationException.class })
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
		
		String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
		//String mensagemDesenvolvedor = ex.toString();
		String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		
		
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler({ AcompanhamentoEstrategicoExistenteException.class })
	public ResponseEntity<Object> handleAcompanhamentoEstrategicoExistenteException(AcompanhamentoEstrategicoExistenteException ex, WebRequest request) {
		String mensagemUsuario = ex.getMessage();
		String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	private List<Erro> criarListadeErros(BindingResult bindingResult) {
		
		List<Erro> erros = new ArrayList<>();
		
		for(FieldError fieldError: bindingResult.getFieldErrors()) {
			
			String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		}
		
		
		return erros;
	}

	//**********************************************************************************************************************************************************


	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<?> handleentidadeNaoEncontradaException(EntidadeNaoEncontradaException ex, WebRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;
		ProblemTypeEnum problemType = ProblemTypeEnum.RECURSO_NAO_ENCONTRADO;
		String detail = ex.getMessage();

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();

		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<?> handleNegocioException(NegocioException ex, WebRequest request) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		ProblemTypeEnum problemType = ProblemTypeEnum.ERRO_NEGOCIO;
		String detail = ex.getMessage();

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();

		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}

	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<?> handleEntidadeEmUsoException(EntidadeEmUsoException ex, WebRequest request) {

		HttpStatus status = HttpStatus.CONFLICT;
		ProblemTypeEnum problemType = ProblemTypeEnum.ENTIDADE_EM_USO;
		String detail = ex.getMessage();

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail).build();

		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		ProblemTypeEnum problemType = ProblemTypeEnum.ERRO_DE_SISTEMA;
		String detail = ERRO_INTERNO_500;

		// Importante colocar o printStackTrace (pelo menos por enquanto, que não estamos
		// fazendo logging) para mostrar a stacktrace no console
		// Se não fizer isso, você não vai ver a stacktrace de exceptions que seriam importantes
		// para você durante, especialmente na fase de desenvolvimento
		ex.printStackTrace();

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();

		return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
																   HttpHeaders headers, HttpStatus status, WebRequest request) {

		ProblemTypeEnum problemType = ProblemTypeEnum.RECURSO_NAO_ENCONTRADO;
		String detail = String.format(RECURSO_NAO_ENCONTRADO, ex.getRequestURL());

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail)
				.build();

		return handleExceptionInternal(ex, problem, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
														HttpStatus status, WebRequest request) {

		if (ex instanceof MethodArgumentTypeMismatchException) {
			return handleMethodArgumentTypeMismatch(
					(MethodArgumentTypeMismatchException) ex, headers, status, request);
		}

		return super.handleTypeMismatch(ex, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

		if(body == null) {
			body = Problem.builder()
					.status(status.value())
					.timestamp(LocalDateTime.now())
					.title(status.getReasonPhrase())
					.mensagemUsuario(ERRO_INTERNO_500)
					.build();
		} else if(body instanceof String) {
			body = Problem.builder()
					.status(status.value())
					.timestamp(LocalDateTime.now())
					.title((String) body)
					.mensagemUsuario(ERRO_INTERNO_500)
					.build();
		}
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		String path = joinPath(ex.getPath());

		ProblemTypeEnum problemType = ProblemTypeEnum.MENSAGEM_INCOMPREENSIVEL;

		String detail = String.format(PROPRIEDADE_COM_VALOR_INVALIDO_ERRO, path, ex.getValue(), ex.getTargetType().getSimpleName());

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(ERRO_INTERNO_500)
				.build();

		return handleExceptionInternal(ex, problem, headers, status, request);
	}

	private String joinPath(List<JsonMappingException.Reference> references) {
		return references.stream()
				.map(ref -> ref.getFieldName())
				.collect(Collectors.joining("."));
	}

	private ResponseEntity<Object> handlePropertyBindingException(PropertyBindingException ex,
																  HttpHeaders headers, HttpStatus status, WebRequest request) {

		String path = joinPath(ex.getPath());

		ProblemTypeEnum problemType = ProblemTypeEnum.MENSAGEM_INCOMPREENSIVEL;
		String detail = String.format(PROPRIEDADE_INEXISTENTE, path);

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(ERRO_INTERNO_500)
				.build();

		return handleExceptionInternal(ex, problem, headers, status, request);
	}

	private Problem.ProblemBuilder createProblemBuilder(HttpStatus status, ProblemTypeEnum problemType, String detail) {

		return Problem.builder()
				.status(status.value())
				.title(problemType.getTitle())
				.detail(detail)
				.timestamp(LocalDateTime.now());
	}


	private ResponseEntity<Object> handleMethodArgumentTypeMismatch(
			MethodArgumentTypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		ProblemTypeEnum problemType = ProblemTypeEnum.PARAMETRO_INVALIDO;

		String detail = String.format(PARAMETRO_INVALIDO, ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

		Problem problem = createProblemBuilder(status, problemType, detail)
				.mensagemUsuario(detail).build();

		return handleExceptionInternal(ex, problem, headers, status, request);
	}



	public static class Erro {

		private String mensagemUsuario;
		private String mensagemDesenvolvedor;

		public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

		public String getMensagemUsuario() {
			return mensagemUsuario;
		}

		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}

	}

}
