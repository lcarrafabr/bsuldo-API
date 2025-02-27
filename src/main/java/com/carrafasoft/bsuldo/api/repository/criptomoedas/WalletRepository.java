package com.carrafasoft.bsuldo.api.repository.criptomoedas;

import com.carrafasoft.bsuldo.api.model.criptomoedas.Wallets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallets, Long> {


    @Query(nativeQuery = true,
    value = "select * from wallets " +
            "where pessoa_id = :pessoaId ")
    List<Wallets> findAllByPessoaId(@Param("pessoaId") Long pessoaId);

    @Query(nativeQuery = true,
    value = "select * from wallets " +
            "where pessoa_id = :pessoaId " +
            "and codigo_wallet = :codigoWallet ")
    Optional<Wallets> findByCodigoWalletAndPessoaId(@Param("codigoWallet") String codigoWallet,
                                           @Param("pessoaId") Long pessoaId);

    @Query(nativeQuery = true,
    value = "delete from wallets " +
            "where codigo_wallet = :codigoWallet ")
    void deleteByCodigoWallet(@Param("codigoWallet") String codigoWallet);

    Optional<Wallets> findByCodigoWallet(@Param("codigoWallet") String codigoWallet);
}
