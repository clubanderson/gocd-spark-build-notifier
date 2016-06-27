package in.ashwanthkumar.gocd.slack.ruleset;

import in.ashwanthkumar.utils.collections.Sets;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class RulesReaderTest {

    @Test
    public void shouldReadTestConfig() {
        Rules rules = RulesReader.read("configs/test-config-1.conf");
        assertThat(rules.isEnabled(), is(true));
        assertThat(rules.getSlackChannel(), is("#gocd"));
        assertThat(rules.getGoServerHost(), is("http://localhost:8080/"));
        assertThat(rules.getPipelineRules().size(), is(2));
        assertThat(rules.getPipelineRules().size(), is(2));
        assertThat(rules.getDisplayMaterialChanges(), is(false));

        PipelineRule pipelineRule1 = new PipelineRule()
                .setNameRegex("gocd-slack-build-notifier")
                .setStageRegex(".*")
                .setChannel("#gocd")
                .setStatus(Sets.of(PipelineStatus.FAILED));
        assertThat(rules.getPipelineRules(), hasItem(pipelineRule1));

        PipelineRule pipelineRule2 = new PipelineRule()
                .setNameRegex("my-java-utils")
                .setStageRegex("build")
                .setChannel("#gocd-build")
                .setStatus(Sets.of(PipelineStatus.FAILED));
        assertThat(rules.getPipelineRules(), hasItem(pipelineRule2));

        assertThat(rules.getPipelineListener(), notNullValue());
    }

    @Test
    public void shouldReadMinimalConfig() {
        Rules rules = RulesReader.read("configs/test-config-minimal.conf");

        assertThat(rules.isEnabled(), is(true));

        assertThat(rules.getGoLogin(), is("someuser"));
        assertThat(rules.getGoPassword(), is("somepassword"));
        assertThat(rules.getGoServerHost(), is("http://localhost:8153/"));
        assertThat(rules.getWebHookUrl(), is("https://hooks.slack.com/services/"));

        assertThat(rules.getSlackChannel(), is("#build"));
        assertThat(rules.getSlackDisplayName(), is("gocd-slack-bot"));
        assertThat(rules.getSlackUserIcon(), is("http://example.com/slack-bot.png"));

        // Default rules
        assertThat(rules.getPipelineRules().size(), is(1));
        assertThat(rules.getDisplayMaterialChanges(), is(true));

        PipelineRule pipelineRule = new PipelineRule()
                .setNameRegex(".*")
                .setStageRegex(".*")
                .setChannel("#build")
                .setStatus(Sets.of(PipelineStatus.CANCELLED, PipelineStatus.BROKEN, PipelineStatus.FAILED, PipelineStatus.FIXED));
        assertThat(rules.getPipelineRules(), hasItem(pipelineRule));

        assertThat(rules.getPipelineListener(), notNullValue());
    }

    @Test
    public void shouldReadMinimalConfigWithPipeline() {
        Rules rules = RulesReader.read("configs/test-config-minimal-with-pipeline.conf");
        assertThat(rules.isEnabled(), is(true));
        assertThat(rules.getSlackChannel(), nullValue());
        assertThat(rules.getGoServerHost(), is("https://go-instance:8153/"));
        assertThat(rules.getWebHookUrl(), is("https://hooks.slack.com/services/"));
        assertThat(rules.getPipelineRules().size(), is(1));

        PipelineRule pipelineRule = new PipelineRule()
                .setNameRegex(".*")
                .setStageRegex(".*")
                .setChannel("#foo")
                .setStatus(Sets.of(PipelineStatus.FAILED));
        assertThat(rules.getPipelineRules(), hasItem(pipelineRule));

        assertThat(rules.getPipelineListener(), notNullValue());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfConfigInvalid() {
        RulesReader.read("test-config-invalid.conf");
    }


}
