package unipar.br.pav.turma.telas.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wb.swt.ResourceManager;

import unipar.br.pav.turma.Activator;
import unipar.br.pav.turma.aplicacao.exception.ValidationException;
import unipar.br.pav.turma.aplicacao.helper.MessageHelper;
import unipar.br.pav.turma.aplicacao.helper.ValidatorHelper;
import unipar.br.pav.turma.telas.dialog.ErroDialog;

public abstract class AbstractEditor extends EditorPart implements ISaveablePart2{
	//Cada Editor que implentar deverá criar o seu FormToolkit, por causa do WindowBuilder
	private FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	/** O {@code form} que será base para criação dos campos. */
	protected ScrolledForm form;
	protected Button btnSalvar;
	private Button btnExcluir;
	private Button btnDesativar;
	private Composite compositeBottom;
	
	private boolean bottomVisible = true;
	private boolean showExcluir = true;
	private boolean showDesativar = false;
	private boolean showSalvar = true;
	private String textoExcluir;
	private String textoDesativar;
	private String textoSalvar;
	private String mensagemExcluir;
	private String mensagemDesativar;

	public AbstractEditor() {}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(parent, SWT.NONE);
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);
		composite.setLayout(new GridLayout(1, false));
		
		form = formToolkit.createScrolledForm(composite);
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(form);
		form.getBody().setLayout(new GridLayout(1, false));
		form.setExpandHorizontal(true);
		form.setExpandVertical(true);

		Composite compositeTop = formToolkit.createComposite(form.getBody(), SWT.NONE);
		compositeTop.setLayout(new GridLayout(6, false));
		compositeTop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(compositeTop);
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(composite, SWT.NONE | SWT.H_SCROLL);
//		scrolledComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(scrolledComposite);
		formToolkit.paintBordersFor(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		compositeBottom = new Composite(scrolledComposite, SWT.BORDER);

		compositeBottom.setVisible(bottomVisible);
		compositeBottom.setLayout(new GridLayout(20, false));
		compositeBottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(compositeBottom);
		formToolkit.paintBordersFor(compositeBottom);
		
		//CRIA O BOTÃO DE SALVAR
		this.btnSalvar = new Button(compositeBottom, SWT.NONE);
		this.btnSalvar.setVisible(showSalvar);
		this.btnSalvar.setImage(ResourceManager.getPluginImage("funcoes/save-gnome32.png"));
		this.btnSalvar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				salvarRegistro();
			}
		});
		this.btnSalvar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		formToolkit.adapt(this.btnSalvar, true, true);
		this.btnSalvar.setText("Salvar | F12");
		//MODIFICAR TEXTO DO BOTÃO DE SALVAR
		if(!textoSalvar.trim().equals(""))
			this.btnSalvar.setText(textoSalvar);
		
		//CRIA O BOTÃO EXCLUIR
		if(showExcluir){
			this.btnExcluir = new Button(compositeBottom, SWT.NONE);
			this.btnExcluir.setImage(ResourceManager.getPluginImage("funcoes/delete-gnome32.png"));
			this.btnExcluir.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (MessageHelper.openConfirm(mensagemExcluir))
						excluirDesativarRegistro();
				}
			});
			this.btnExcluir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			formToolkit.adapt(this.btnExcluir, true, true);
			this.btnExcluir.setText("Excluir | F11");
			//MODIFICAR TEXTO DO BOTÃO DE EXCLUIR
			if(textoExcluir != null && textoExcluir.trim().equals(""))
				this.btnExcluir.setText(textoExcluir);
			//ADEQUAR MENSAGEM DE EXCLUIR
			if(mensagemExcluir != null && mensagemExcluir.trim().equals(mensagemExcluir))
				mensagemExcluir = "Tem certeza que deseja excluir o registro?";
		}
		
		//CRIA O BOTÃO DESATIVAR
		if(showDesativar){
			this.btnDesativar = new Button(compositeBottom, SWT.NONE);
			this.btnDesativar.setImage(ResourceManager.getPluginImage("desativar/desativar32.png"));
			this.btnDesativar.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (MessageHelper.openConfirm(mensagemDesativar))
						excluirDesativarRegistro();
				}
			});
			this.btnDesativar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			formToolkit.adapt(this.btnDesativar, true, true);
			this.btnDesativar.setText("Desativar | F11");
			//MODIFICAR TEXTO DO BOTÃO DE DESATIVAR
			if(textoDesativar != null && !textoDesativar.trim().equals(textoDesativar))
				this.btnDesativar.setText(textoDesativar);
			//ADEQUAR MENSAGEM DE DESATIVAR
			if(mensagemDesativar != null && mensagemDesativar.trim().equals(""))
				mensagemDesativar = "Tem certeza que deseja desativar o registro?";
		}
		
		addComponents(compositeTop);
		
		scrolledComposite.setContent(compositeBottom);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, 25));
		scrolledComposite.getHorizontalBar().setPageIncrement(1);
		scrolledComposite.getHorizontalBar().setIncrement(1);
		
		form.setContent(compositeTop);
		form.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		form.getVerticalBar().setPageIncrement(15);
		form.getVerticalBar().setIncrement(15);
	}
	
	/**
	 * Método para implementar ação para o botão 'Salvar'
	 */
	protected abstract void salvarRegistro();

	/**
	 * Método para implementar ação para o botão 'Excluir' ou 'Desativar'
	 */
	protected abstract void excluirDesativarRegistro();

	protected abstract void addComponents(Composite compositeTop);
	
	protected Button addBotao(String texto, final Action acao, String path, boolean visible){
		if(!visible)
			return null;
		
		Button btnNewButton = new Button(this.compositeBottom, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				acao.run();
			}
		});
		btnNewButton.setImage(ResourceManager.getPluginImage(Activator.PLUGIN_ID, path));
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		formToolkit.adapt(btnNewButton, true, true);
		btnNewButton.setText(texto);
		
		return btnNewButton;
	}
	
	/**
	 * Adiciona o botão para reativar o registro.
	 *
	 * @param acao a ação do botão, quando pressionado
	 * @param visible indicativo ou codição para visualização do botão
	 * @return o botão de desativar
	 * @since 4.25.0
	 */
	protected Button addBotaoReativar(final Action acao, boolean visible){
		return addBotao("Reativar", acao, "assets/redo/redo32.png", visible);
	}

	/**
	 * Fecha o {@code Editor} que está em ativo.
	 */
	protected void closeThisEditor() {
		getEditorSite().getPart().getSite().getWorkbenchWindow().getActivePage().closeEditor(this, false);
	}
	
	@Override
	public void setFocus() {}
	
	@Override
	public void doSaveAs() {}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		monitor.beginTask("Salvando registro...", IProgressMonitor.UNKNOWN);
		salvarRegistro();
		monitor.done();
	}

	public void doDeleteDisable(IProgressMonitor monitor) {
		monitor.beginTask((showExcluir ? "Excluindo" : "Desativando")+" registro...", IProgressMonitor.UNKNOWN);
		excluirDesativarRegistro();
		monitor.done();
	}
	
	protected void addEnterNextListener(final Control controle){
		controle.addTraverseListener(new TraverseListener() {
			
			@Override
			public void keyTraversed(TraverseEvent e) {
				if(e.character == SWT.CR)
					controle.traverse(SWT.TRAVERSE_TAB_NEXT);
			}
		});
	}
	
	public void setShowDesativar(boolean showDesativar) {
		this.showDesativar = showDesativar;
		//SE FOR DESATIVAR, NÃO ATIVA O EXCLUIR
		this.showExcluir = false;
	}

	public void setShowExcluir(boolean showExcluir) {
		this.showExcluir = showExcluir;
		//SE FOR EXCLUIR, NÃO ATIVA O DESATIVAR
		this.showDesativar = false;
	}
	
	public void setShowSalvar(boolean showSalvar) {
		this.showSalvar = showSalvar;
	}
	
	public void setTextoSalvar(String texto){
		this.textoSalvar = texto;
	}
	
	public void setTextoExcluir(String texto){
		this.textoExcluir = texto;
	}
	
	public void setTextoDesativar(String texto) {
		this.textoDesativar = texto;
	}

	/**
	 * Insere a mensagem de confirmação para excluir registro.
	 *
	 * @param mensagemExcluir a mensagem de excluir
	 */
	public void setMensagemExcluir(String mensagemExcluir) {
		this.mensagemExcluir = mensagemExcluir;
	}
	
	/**
	 * Insere a mensagem de confirmação para desativar registro.
	 *
	 * @param mensagemDesativar a mensagem de desativar
	 */
	public void setMensagemDesativar(String mensagemDesativar) {
		this.mensagemDesativar = mensagemDesativar;
	}

	/**
	 * Definie se será criado os botões na tela.
	 *
	 * @param show informar 'true' para criar os botões
	 */
	public void showBottom(boolean show){
		this.bottomVisible = show;
	}
	
	/**
	 * Modifica a cor de fundo do editor
	 * @param cor a cor escolhida
	 */
	public void setBackgroudColor(Color cor){
		this.formToolkit.setBackground(cor);
	}
	
	/**
	 * Insere o título do {@code Form} do {@code Editor}
	 * @param titulo o título para o form
	 */
	public void setTitulo(String titulo){
		this.form.setText(titulo);
	}
	
	/**
	 * Insere a imagem do {@link #form} do {@code Editor}. Deverá ser informada uma imagem com tamanho 32x32
	 * @param image a imagem do form
	 */
	public void setImagem(Image image){
		form.setImage(image);
	}

	@Override
	public int promptToSaveOnClose() {
		boolean ret = MessageHelper.openConfirm("O registro foi alterado. Deseja salvar as alterações?");
		
		if(ret){
			salvarRegistro();
		}
		
		return CANCEL;
	}
	
	public Button getBotaoExcluir() {
		return btnExcluir;
	}
	
	public Button getBtnDesativar() {
		return btnDesativar;
	}

	/**
	 * Valida o objeto informado usando o {@link ValidatorHelper#validar(Object)} e 
	 * verifica também se o sistema está bloqueado através do {@link BloqueioSistemaHelper#validarSistemaBloqueado()}
	 *
	 * @param obj o objeto a ser validado
	 * @throws ValidationException a exceção da validação do objeto
	 */
	protected void validar(Object obj) throws ValidationException {
		try {
			ValidatorHelper.validar(obj);
		} catch (ValidationException e) {
			throw e;
		}
	}
	
	/**
	 * Abre o {@code ErroDialog} com a mensagem de erro informada
	 *
	 * @param mensagem a mensagem do erro/exceção
	 */
	protected void showValidationDialog(String mensagem) {
		new ErroDialog(mensagem).open();
	}
	
	/**
	 * Obtém a mensagem adequada para desativar ou excluir.
	 *
	 * @return a mensagem de desativar ou de excluir
	 */
	public String getMensagemDesativarExcluir(){
		return showExcluir ? mensagemExcluir : mensagemDesativar;
	}
	
	public void redrawBotoes(){
		compositeBottom.redraw();
	}
}