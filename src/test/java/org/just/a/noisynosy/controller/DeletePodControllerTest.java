package org.just.a.noisynosy.controller;

import org.junit.Before;
import org.junit.Test;
import org.just.a.noisynosy.k8s.KubeService;
import org.mockito.Mockito;

public class DeletePodControllerTest {

  private KubeService service;

  private DeletePodController controller;

  @Before
  public void setup() {
    service = Mockito.mock(KubeService.class);

    controller = new DeletePodController(service);
  }

  @Test
  public void should_delete_the_pod() {
    controller.deletePod("test", "pod-test-42", "my-special-token");

    Mockito.verify(service).deletePod("my-special-token", "test", "pod-test-42");
  }

}
