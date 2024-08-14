package com.example.dbestoque.controllers;

import com.example.dbestoque.models.Produto;
import com.example.dbestoque.repository.ProdutoRepository;
import com.example.dbestoque.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController //Abrindo para receber uma requisição DOS CLIENTES (Controller), devolvendo um JSON (Rest)
@RequestMapping("/api/produtos") //Qual é a rota que a API vai receber; URL base
public class ProdutoController {
    private ProdutoService ProdutoService; //Variável é uma const de JS. AUTOWIRED não funciona com constante, tem que ser uma variável

    private final Validator validator;

    @Autowired
    public ProdutoController(ProdutoService produtoService, Validator validator) {
        this.ProdutoService = produtoService;
        this.validator = validator;
    } //Criando um objeto que o container vai gerenciar (Bean)

    @Operation(summary = "Lista todos os produtos", description = "Retorna uma lista de todos os produtos disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/selecionar")
    public List<Produto> listarProdutos() {
        return ProdutoService.selecionar();
    }

    @Operation(summary = "Insere produto", description = "Insere um novo produto e retorna em json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto inserido com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping("/inserir")
    public ResponseEntity<?> inserirProduto(@Valid @RequestBody Produto produto, BindingResult resultado) {
        if(resultado.hasErrors()){
            StringBuilder errorMessage = new StringBuilder("Especificação:");
            int errorCount = resultado.getFieldErrors().size();
            int count = 0;
            for (FieldError error : resultado.getFieldErrors()) {
                errorMessage.append(" ").append(error.getDefaultMessage());
                if (++count < errorCount) {
                    errorMessage.append(";");
                }
            }
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("erros", errorMessage.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } else {
            ProdutoService.salvarProduto(produto);
            return new ResponseEntity<>(produto, HttpStatus.CREATED);
        }
    }

    @Operation(summary = "Deleta produto", description = "Deleta produto e mostra mensagem.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto removido com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<String> excluirProduto(@Valid @PathVariable Long id, BindingResult resultado){
        Produto produtoExistente = ProdutoService.excluirProduto(id);
        if(produtoExistente != null){
            return ResponseEntity.ok("Produto removido com sucesso");
        }
        return ResponseEntity.status(404).body("Produto não encontrado");
    }


    @Operation(summary = "Atualiza produto", description = "Atualiza produto e retorna ele em json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizarProduto(@Valid @PathVariable Long id, @RequestBody Produto produtoAtualizado, BindingResult resultado) {

        if(resultado.hasErrors()){
            StringBuilder errorMessage = new StringBuilder("Especificação:");
            int errorCount = resultado.getFieldErrors().size();
            int count = 0;
            for (FieldError error : resultado.getFieldErrors()) {
                errorMessage.append(" ").append(error.getDefaultMessage());
                if (++count < errorCount) {
                    errorMessage.append(";");
                }
            }
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("erros", errorMessage.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } else {
            ProdutoService.atualizarProduto(id, produtoAtualizado);
            return new ResponseEntity<>(produtoAtualizado, HttpStatus.CREATED);
        }
    }

    public Map<String, String> validarProduto(BindingResult resultado){
        Map<String, String> erros = new HashMap<>();
        for (FieldError error : resultado.getFieldErrors()){
            erros.put(error.getField(), error.getDefaultMessage());
        }
        return erros;
    }

    @Operation(summary = "Atualiza parcial", description = "Atualiza campos parcialmente do objeto produto e retorna em json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PatchMapping("/atualizarParcial/{id}")
    public ResponseEntity<?> ajustarProdutoParcial(@PathVariable Long id, @RequestBody Map<String,
            Object> updates){

        try{
            Produto produto = ProdutoService.buscarProdutoPorId(id);
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

            //Validar os dados
            DataBinder blinder = new DataBinder(produto);
            blinder.setValidator(validator);
            blinder.validate();
            BindingResult resultado = blinder.getBindingResult();
            if(resultado.hasErrors()){
                Map erros = validarProduto(resultado);
                return ResponseEntity.badRequest().body(erros);
            }
            Produto produtoSalvo = ProdutoService.salvarProduto(produto);
            return ResponseEntity.ok(produtoSalvo);
        }catch(RuntimeException re){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado.");
        }

    }

//    @PatchMapping("/BuscaProdutoPreco/{nome}/{preco}")
//    public List<Produto> findByNomeLikeIgnoreCaseAndPrecoLessThan(@PathVariable String nome, @PathVariable BigDecimal preco, @RequestBody Map<String, Object> updates) {
//        List<Produto> produtos = ProdutoRepository.findByNomeLikeIgnoreCaseAndPrecoLessThan(nome, preco);
//        return produtos;
//    }
//


}