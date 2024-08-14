package com.example.dbestoque.services;

import com.example.dbestoque.models.Produto;
import com.example.dbestoque.repository.ProdutoRepository;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service

public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final Validator validator;

    public ProdutoService(ProdutoRepository produtoRepository, Validator validator){
        this.produtoRepository = produtoRepository;
        this.validator = validator;
    }

    public List<Produto> selecionar(){
        return produtoRepository.findAll();
    }

//    public ResponseEntity<String> inserirProduto(){
//        //A entidade que irá responder será em formato de SPRING. Transforma o JSON em Objeto Produto
//        Produto produtoInserir = produtoRepository.save(produto); //Pegamos o objeto que injetamos a dependência e fazemos o JPA mapear a tabela Produto
//        if(produtoInserir.getId() != 0) {
//            return ResponseEntity.ok("Produto inserido com sucesso");
//        }else{
//            return ResponseEntity.ok("Produto não foi inserido.");
//        }
//    }

    public Produto salvarProduto(Produto produto){
        return produtoRepository.save(produto);
    }

    public Produto buscarProdutoPorId(Long id){
        return produtoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Produto não encontrado"));
    }

    public Produto excluirProduto(Long id){
        Optional <Produto> prod = produtoRepository.findById(id);
        if(prod.isPresent()){
            produtoRepository.deleteById(id);
            return prod.get();
        }
        return null;
    }

    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoRepository.save(produto);
            return produto;
        }
        return null;
    }

    public String atualizarProdutoParcial(Long id, Map<String, Object> updates) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        StringBuilder errorMessage = new StringBuilder("Erros de validação:");
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();

            if (updates.containsKey("nome")) {
                produto.setNome((String) updates.get("nome"));
            }
            if (updates.containsKey("descricao")) {
                produto.setDescricao((String) updates.get("descricao"));
            }
            if (updates.containsKey("preco")) {
                produto.setPreco((Double) updates.get("preco"));
            }
            if (updates.containsKey("quantidadeEstoque")) {
                produto.setQuantidadeEstoque((Integer) updates.get("quantidadeEstoque"));
            }

            BeanPropertyBindingResult erros = new BeanPropertyBindingResult(produto, "produto");

            updates.forEach((propertyName, propertyValue) -> {
                validarEAtualizarPropriedade(produto, propertyName, propertyValue, erros);
            });

            if (erros.hasErrors()) {
                for (FieldError error : erros.getFieldErrors()) {
                    errorMessage.append(" ").append(error.getDefaultMessage()).append(";");
                }
                return errorMessage.toString();
            }

            produtoRepository.save(produto);
            return errorMessage.toString();
        } else {
            return errorMessage.toString();
        }
    }

    private void validarEAtualizarPropriedade(Produto produto, String propertyName, Object propertyValue, BeanPropertyBindingResult errors) {

        //todo: DataBinder é uma ponte entre o modelo de dados e a representação visual ou a lógica de negócio da aplicação.
        DataBinder dataBinder = new DataBinder(produto, "produto"); // Observe que aqui também definimos o nome do objeto
        dataBinder.setValidator(validator);

        // Define o novo valor
        // Usa a classe MutablePropertyValues, que é uma implementação de PropertyValues
        // usada para armazenar e manipular propriedades de objetos.
        // Atualiza o valor da propriedade
        dataBinder.bind(new MutablePropertyValues(Collections.singletonMap(propertyName, propertyValue)));

        // Realiza a validação
        dataBinder.validate();

        // Se houver erros de validação, os adicionamos ao objeto errors original
        if (dataBinder.getBindingResult().hasErrors()) {

            errors.addAllErrors(dataBinder.getBindingResult());
        } else {
            try {
                Field campo = Produto.class.getDeclaredField(propertyName);
                campo.setAccessible(true);
                campo.set(produto, propertyValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                errors.rejectValue(propertyName, "invalidProperty", "Não foi possível acessar a propriedade: " + propertyName);
            }
        }
    }

//    public Produto findByNomeLikeIgnoreCaseAndPrecoLessThan(@RequestParam String nome, @RequestParam BigDecimal preco){
//        ProdutoRepository
//    }


}

//    public Produto atualizarProdutoParcialmente (Long id, Map<String, Object> updates) {
//        Optional<Produto> produtoExistente = produtoRepository.findById(id);
//
//        if(produtoExistente.isPresent()) {
//            Produto produto = produtoExistente.get();
//
//            // Atualiza apenas os campos que foram passados no corpo da requisição
//            if (updates.containsKey("nome")) {
//                produto.setNome((String) updates.get("nome"));
//            }
//            if (updates.containsKey("descricao")) {
//                produto.setDescricao((String) updates.get("descricao"));
//            }
//            if (updates.containsKey("preco")) {
//                try {
//                    produto.setPreco((Double) updates.get("preco"));
//                } catch (ClassCastException ce) {
//                    String precoString = updates.get("preco").toString();
//                    produto.setPreco(Double.parseDouble(precoString));
//                }
//            }
//            if (updates.containsKey("quantidadeEstoque")) {
//                produto.setQuantidadeEstoque((Integer) updates.get("quantidadeEstoque"));
//            }
//            produtoRepository.save(produto);
//            return produto;
//        } else {
//            return null;
//        }
//    }




