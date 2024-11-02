package br.com.fiap.techchallenge.lanchonete.gateways;

import br.com.fiap.techchallenge.lanchonete.adapters.mappers.ProdutoMapper;
import br.com.fiap.techchallenge.lanchonete.entities.MensagemErroPadrao;
import br.com.fiap.techchallenge.lanchonete.entities.Produto;
import br.com.fiap.techchallenge.lanchonete.entities.dbEntities.ProdutoEntity;
import br.com.fiap.techchallenge.lanchonete.interfaces.dbconnection.RepositoryProduto;
import br.com.fiap.techchallenge.lanchonete.interfaces.gateways.ProdutoGateway;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProdutoGatewayImpl implements ProdutoGateway {

    private final RepositoryProduto repositoryProduto;
    private final ProdutoMapper produtoMapper;

    @Override
    public Produto save(Produto produto) throws Exception {
        try {
            var produtoEntity = produtoMapper.toDbEntity(produto);
            produtoEntity = repositoryProduto.save(produtoEntity);

            return produtoMapper.fromDbEntityToEntity(produtoEntity);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage(), exception);
        }
    }

    @Override
    public Produto update(Produto produto) throws Exception {
        try {
            var produtoOptional = repositoryProduto.findById(produto.getId());

            var produtoEntity = aplicaAlteracoes(produto, produtoOptional);

            return produtoMapper.fromDbEntityToEntity(produtoEntity);
        } catch (EntityNotFoundException entityNotFoundException) {
            throw new EntityNotFoundException(MensagemErroPadrao.PRODUTO_NAO_ENCONTRADO, entityNotFoundException);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage(), exception);
        }
    }

    private ProdutoEntity aplicaAlteracoes(Produto produto, Optional<ProdutoEntity> produtoEntityOptional) {

        if (produtoEntityOptional.isEmpty())
            throw new EntityNotFoundException();

        var produtoEntity = produtoEntityOptional.get();
        produtoEntity = produtoMapper.toProdutoEntityAtualizado(produto);

        return repositoryProduto.save(produtoEntity);
    }

    @Override
    public void delete(Integer id) throws Exception {
        try {
            repositoryProduto.deleteById(id);
        } catch (Exception exception) {
            throw new Exception(MensagemErroPadrao.ERRO_GENERICO, exception);
        }
    }

    @Override
    public List<Produto> buscaPorCategoria(String categoria) throws Exception {
        try {
            var produtoEntityOptional = repositoryProduto.findByCategoria(categoria);

            if (produtoEntityOptional.isEmpty())
                throw new EntityNotFoundException(String.format(MensagemErroPadrao.PRODUTO_NAO_ENCONTRADO_CATEGORIA, categoria));

            return produtoEntityOptional.get()
                    .stream()
                    .map(produtoMapper::fromDbEntityToEntity)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException entityNotFoundException) {
            throw new EntityNotFoundException(entityNotFoundException.getLocalizedMessage(), entityNotFoundException);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage(), exception);
        }
    }

    @Override
    public Boolean isProduto(Integer id) {
        var produtoOptional = repositoryProduto.findById(id);

        return produtoOptional.isPresent();
    }

    @Override
    public Produto buscaPorId(Integer id) throws Exception {
        try {
            var produtoEntityOptional = repositoryProduto.findById(id);

            if(produtoEntityOptional.isEmpty())
                throw new EntityNotFoundException(String.format(MensagemErroPadrao.PRODUTO_NAO_ENCONTRADO, id));

            return produtoMapper.fromDbEntityToEntity(produtoEntityOptional.get());
        } catch (EntityNotFoundException entityNotFoundException) {
            throw new EntityNotFoundException(entityNotFoundException.getLocalizedMessage(), entityNotFoundException);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage(), exception);
        }
    }
}

