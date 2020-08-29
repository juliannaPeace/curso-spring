package com.juliannapaz.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juliannapaz.cursomc.domain.Pedido;
import com.juliannapaz.cursomc.exceptions.ObjectNotFoundException;
import com.juliannapaz.cursomc.repositories.PedidoRepository;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	public Pedido find(Integer id) {
		Optional<Pedido> pedido = pedidoRepository.findById(id);

		return pedido.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ",tipo:" + Pedido.class.getName()));
	}
}
