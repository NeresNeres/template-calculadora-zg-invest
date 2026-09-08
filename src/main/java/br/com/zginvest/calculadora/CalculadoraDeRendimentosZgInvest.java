package br.com.zginvest.calculadora;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalculadoraDeRendimentosZgInvest {

	private final List<Negociacao> negociacoes;
	private final Map<LocalDate, BigDecimal> precosFechamento;

	public CalculadoraDeRendimentosZgInvest(List<Negociacao> negociacoes, Map<LocalDate, BigDecimal> precosFechamento) {
		this.negociacoes = negociacoes.stream()
				.sorted(Comparator.comparing(Negociacao::data))
				.collect(Collectors.toList());
		this.precosFechamento = precosFechamento;
	}

	public PosicaoCarteira calcularPosicaoEm(LocalDate data) {

		BigDecimal precoFechamento = this.precosFechamento.get(data);

		if (precoFechamento == null) {
			throw new IllegalArgumentException("Não existe preço de fechamento para a data " + data);
		}

		long quantidadeAcoes = 0;
		BigDecimal valorInvestido = BigDecimal.ZERO;

		for (Negociacao negociacao : this.negociacoes) {

			if (negociacao.data().isAfter(data)) {
				break;
			}

			BigDecimal valorOperacao = negociacao.preco().multiply(BigDecimal.valueOf(negociacao.quantidade()));

			if (negociacao.tipo() == OperacaoNegociacao.COMPRA) {

				quantidadeAcoes += negociacao.quantidade();
				valorInvestido = valorInvestido.add(valorOperacao);

			} else {

				quantidadeAcoes -= negociacao.quantidade();
				valorInvestido = valorInvestido.subtract(valorOperacao);
			}
		}

		BigDecimal saldoAtual = precoFechamento
				.multiply(BigDecimal.valueOf(quantidadeAcoes))
				.setScale(2, java.math.RoundingMode.HALF_UP);

		BigDecimal rendimentoPercentual = saldoAtual
				.subtract(valorInvestido)
				.multiply(BigDecimal.valueOf(100))
				.divide(valorInvestido, 2, java.math.RoundingMode.HALF_UP);

		if (valorInvestido.compareTo(BigDecimal.ZERO) == 0) {
			rendimentoPercentual = BigDecimal.ZERO;
		}

		return new PosicaoCarteira(
				data,
				quantidadeAcoes,
				saldoAtual,
				rendimentoPercentual);
	}
}
