package com.codegik.gitflow.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;

import com.codegik.gitflow.AbstractGitFlowMojo;
import com.codegik.gitflow.mojo.util.BranchUtil;
import com.codegik.gitflow.mojo.util.GitFlow;
import com.codegik.gitflow.mojo.util.MergeGitFlow;


/**
 * Put last tag from release on master
 * Delete all related branches (release|feature|bugfix)
 *
 * @author Inacio G Klassmann
 */
@Mojo(name = "publish-release", aggregator = true)
public class PublishReleaseMojo extends AbstractGitFlowMojo {

    @Parameter( property = "version", required = true )
	private String version;


	@Override
	public void run(GitFlow gitFlow) throws Exception {
		validadeBefore(gitFlow);

		// Busca ultima tag da release
		Ref tagRef = gitFlow.findLastTag(getVersion());
		if (tagRef == null) {
			throw new MojoExecutionException("The release " + getVersion() + " was never finished, please execute finish-release goal before!");
		}

		// Realiza o merge da tag para o master (using theirs)
		gitFlow.checkoutBranch(MASTER);

		MergeGitFlow mergeGitFlow = new MergeGitFlow();
		mergeGitFlow.setBranchName(MASTER);
		mergeGitFlow.setErrorMessage("publish-release -Dversion=" + getVersion());
		mergeGitFlow.setTargetRef(tagRef);

		/**
		 * TODO
		 * Como resolver a situacao que ocorre quando um hotfix eh aberto e entregue para producao
		 * durante o periodo de homologacao de uma release?
		 * Solucao: replicar as correcoes de hotfix para a versao que esta em homologacao
		 */
		gitFlow.merge(mergeGitFlow, MergeStrategy.THEIRS);
		compileProject();
		gitFlow.push();

		// Remove os branches de feature, bugfix e o branch da release
		gitFlow.deleteRemoteBranch(getVersion(), BranchType.feature);
		gitFlow.deleteRemoteBranch(getVersion(), BranchType.bugfix);
		gitFlow.deleteRemoteBranch(BranchUtil.buildReleaseBranchName(getVersion()));
	}


	private void validadeBefore(GitFlow gitFlow) throws Exception {
		gitFlow.validadePatternReleaseVersion(getVersion());
	}


	@Override
	public void rollback(GitFlow gitFlow, Exception e) throws MojoExecutionException {
		try {
			getLog().error(e.getMessage());
			getLog().info("Rolling back all changes");
			gitFlow.reset(MASTER);
			gitFlow.checkoutBranchForced(MASTER);
		} catch (Exception e1) {;}
		throw new MojoExecutionException("ERROR", e);
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}
}
