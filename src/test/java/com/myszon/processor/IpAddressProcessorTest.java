package com.myszon.processor;

import com.myszon.api.GithubApiClient;
import com.myszon.model.Alias;
import com.myszon.repository.IIndexIngest;
import com.myszon.repository.IIndexManager;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@MicronautTest
//@Requires(property = "mockito.test.enabled", defaultValue = StringUtils.FALSE, value = StringUtils.TRUE)
class IpAddressProcessorTest {

    @Inject
    IpAddressProcessor processor;



    @Test
    public void getAliases_shouldReturn_IP_ADDRESS_and_IP_ADDRESS_RANGE() {
        Set<Alias> expected = Set.of(Alias.IP_ADDRESS, Alias.IP_ADDRESS_RANGE);

        Set<Alias> actual = this.processor.getAliases();

        assertEquals(expected, actual);
    }

    @Test
    public void refreshIndex() {
        Set<Alias> expected = Set.of(Alias.IP_ADDRESS, Alias.IP_ADDRESS_RANGE);

        Set<Alias> actual = this.processor.getAliases();

        assertEquals(expected, actual);
    }


    @MockBean(IIndexManager.class)
    IIndexManager indexManager() {
        return mock(IIndexManager.class);
    }

    @MockBean(GithubApiClient.class)
    GithubApiClient githubApiClient() {
        return mock(GithubApiClient.class);
    }

    @MockBean(IIndexIngest.class)
    IIndexIngest iIndexIngest() {
        return mock(IIndexIngest.class);
    }
}