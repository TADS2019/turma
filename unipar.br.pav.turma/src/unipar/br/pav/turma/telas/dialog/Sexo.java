package unipar.br.pav.turma.telas.dialog;

public enum Sexo {
	
	MASCULINO("M", "Masculino"),
	FEMININO("F", "Feminino"),
	BISEXUAL("B", "Bisexual"),
	ANDR�GINO("A", "Andr�gino"),
	BIGENERO("2", "Big�nero"),
	TRANSSEXUAL("T", "Transsexual");
	
	private Sexo(String sigla, String descricao){
		this.sigla = sigla;
		this.descricao = descricao;
	}
	
	private String sigla;
	private String descricao;
	
	public String getSigla() {
		return sigla;
	}
	
	public String getDescricao() {
		return descricao;
	}
}
