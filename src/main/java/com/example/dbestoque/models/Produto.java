package com.example.dbestoque.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Representa um produto no sistema.")
@Entity
public class Produto {
    @Schema(description = "Identificador único do produto.", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Schema(description = "Nome do produto.", example = "Patinho Moído.")
    @NotNull(message="O nome não pode ser nulo")
    @Size(min = 2, message = "O nome deve ter no mínimo caracteres")
    private String nome;

    @Schema(description = "Descrição do produto.", example = "O patinho moído é uma escolha excelente para quem busca uma carne bovina magra e versátil.")
    private String descricao;

    @Schema(description = "Preço do produto.", example = "22.32")
    @NotNull(message="O preço não pode ser nulo")
    @Min(value=1, message = "O preço tem que existir")
    private double preco;

    @Schema(description = "Quantidade do estoque.", example = "12")
    @NotNull(message="A quantidade não pode ser nulo")
    @Min(value=1, message = "O estoque deve ter no mínimo um produto")
    @Column(name="quantidadeestoque")
    private int quantidadeEstoque;


    //Método Construtor
    public Produto(String nome, String descricao, double preco, int quantidadeEstoque ){
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Produto(){

    }

    //Métodos getters e setters


    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
}
