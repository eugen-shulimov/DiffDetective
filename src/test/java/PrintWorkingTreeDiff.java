import datasets.ParseOptions.DiffStoragePolicy;
import datasets.Repository;
import diff.CommitDiff;
import diff.GitDiffer;
import diff.PatchDiff;
import diff.result.CommitDiffResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Perform "git diff" on a git repository.
 */
public class PrintWorkingTreeDiff {
	
	@Test
	public void testWorkingTreeDiff() throws IOException, NoHeadException, GitAPIException {
		String repoName = "test_repo";
		
		// Retrieve repository
		final String repo_path = "repositories/" + repoName; 
		final Repository repository = Repository.fromZip(Paths.get(repo_path + ".zip"), repoName); // remove ".zip" when using fromDirectory()
		repository.setParseOptions(repository.getParseOptions().withDiffStoragePolicy(DiffStoragePolicy.REMEMBER_FULL_DIFF));
		
		final GitDiffer differ = new GitDiffer(repository);
		
		// Retrieve latest commit
		// Alternatively, replace with desired RevCommit
		RevCommit latestCommit = differ.getJGitRepo().log().setMaxCount(1).call().iterator().next(); 
		
		// Extract CommitDiff
		CommitDiffResult commitDiffResult = GitDiffer.createWorkingTreeDiff(differ.getJGitRepo(), repository.getDiffFilter(), latestCommit, repository.getParseOptions());
		CommitDiff commitDiff = commitDiffResult.diff().orElseThrow();
		
		// Save diff output
		String diffOutput = "";
		for (PatchDiff patchDiff : commitDiff.getPatchDiffs()) {
			diffOutput += patchDiff.getDiff();
		}
		
		// Load diff to verfiy computed output
		String fileForVerification = "src/test/resources/" + repoName + ".txt";
		String result = read(Paths.get(fileForVerification));

		// Remove all white spaces to simplify comparison 
		diffOutput = diffOutput.replaceAll("\\s", "");
		result = result.replaceAll("\\s", "");
	
		// Check whether diffs match
		Assert.assertTrue(diffOutput.equals(result));
		
	}
	
	/**
	 * Read in diff from external file.
	 * 
	 * @param filePath Path to the file
	 * @return The diff
	 * @throws IOException 
	 */
	private static String read(Path filePath) throws IOException {
        return util.IO.readAsString(filePath);
	}
	
}
