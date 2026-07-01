package br.com.zginvest.calculadora;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Negociacao(LocalDate data, TipoOperacao tipo, int quantidade, BigDecimal preco) {
}
