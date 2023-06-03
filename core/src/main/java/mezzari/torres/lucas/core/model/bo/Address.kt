package mezzari.torres.lucas.core.model.bo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author Lucas T. Mezzari
 * @since 30/05/2023
 */
class Address (
    @SerializedName("cep")
    val cep: String,
    @SerializedName("logradouro")
    val street: String,
    @SerializedName("complemento")
    val complement: String,
    @SerializedName("bairro")
    val neighborhood: String,
    @SerializedName("localidade")
    val locality: String,
    @SerializedName("uf")
    val state: String,
    @SerializedName("unidade")
    val unity: String,
    @SerializedName("ibge")
    val ibge: String,
    @SerializedName("gia")
    val gia: String
): Serializable