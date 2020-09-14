package com.juliannapaz.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juliannapaz.cursomc.domain.ItemPedido;
import com.juliannapaz.cursomc.domain.PagamentoComBoleto;
import com.juliannapaz.cursomc.domain.Pedido;
import com.juliannapaz.cursomc.domain.enums.EstadoPagamento;
import com.juliannapaz.cursomc.exceptions.ObjectNotFoundException;
import com.juliannapaz.cursomc.repositories.ItemPedidoRepository;
import com.juliannapaz.cursomc.repositories.PagamentoRepository;
import com.juliannapaz.cursomc.repositories.PedidoRepository;
import com.juliannapaz.cursomc.repositories.ProdutoRepository;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	public Pedido find(Integer id) {
		Optional<Pedido> pedido = pedidoRepository.findById(id);

		return pedido.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ",tipo:" + Pedido.class.getName()));
	}

	@Transactional
	public Pedido insert(Pedido pedido) {
		pedido.setId(null);
		pedido.setInstante(new Date());
		pedido.getPagamento().setEstadoPagamento(EstadoPagamento.PENDENTE);
		pedido.getPagamento().setPedido(pedido);
		if (pedido.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagBoleto = (PagamentoComBoleto) pedido.getPagamento();
			BoletoService.preencherPagamentoComBoleto(pagBoleto, pedido.getInstante());
		}

		pedido = pedidoRepository.save(pedido);
		pagamentoRepository.save(pedido.getPagamento());

		for (ItemPedido ip : pedido.getItens()) {
			ip.setDesconto(0d);
			ip.setPreco(produtoRepository.findById(ip.getProduto().getId()).get().getPreco());
			ip.setPedido(pedido);
		}
		
		itemPedidoRepository.saveAll(pedido.getItens());
		
		return pedido;
	}
}
