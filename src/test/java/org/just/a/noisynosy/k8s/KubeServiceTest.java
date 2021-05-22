package org.just.a.noisynosy.k8s;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class KubeServiceTest {

  private KubeClient client;

  private KubeService service;

  @Before
  public void setup() {
    client = Mockito.mock(KubeClient.class);

    service = new KubeService(client);
  }

  @Test
  public void should_close_the_client() throws Exception {
    service.close();

    Mockito.verify(client).close();
  }

  @Test
  public void should_delete_pod() throws Exception {
    service.acceptToken("aaa", "test", "test-pod-42");

    service.deletePod("aaa", "test", "test-pod-42");

    Mockito.verify(client).deletePod("test", "test-pod-42");
  }

  @Test(expected = RuntimeException.class)
  public void should_throw_on_no_token_given() throws Exception {
    service.acceptToken("aaa", "test", "test-pod-42");

    service.deletePod(null, "test", "test-pod-42");
  }

  @Test(expected = RuntimeException.class)
  public void should_throw_on_unknown_token() throws Exception {
    service.deletePod("aaa", "test", "test-pod-42");
  }

  @Test(expected = RuntimeException.class)
  public void should_throw_on_unknown_token_second() throws Exception {
    service.acceptToken("aaa", "test", "test-pod-42");

    service.deletePod("bbb", "test", "test-pod-42");
  }

}
