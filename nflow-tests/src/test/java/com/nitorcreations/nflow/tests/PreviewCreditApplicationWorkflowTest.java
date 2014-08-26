package com.nitorcreations.nflow.tests;

import static java.util.Arrays.asList;
import static org.apache.cxf.jaxrs.client.WebClient.fromClient;
import static org.hamcrest.Matchers.notNullValue;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nitorcreations.nflow.rest.v0.msg.*;
import com.nitorcreations.nflow.tests.demo.CreditApplicationWorkflow;
import com.nitorcreations.nflow.tests.runner.NflowServerRule;

@FixMethodOrder(NAME_ASCENDING)
public class PreviewCreditApplicationWorkflowTest extends AbstractNflowTest {

  @ClassRule
  public static NflowServerRule server = new NflowServerRule.Builder().build();

  public PreviewCreditApplicationWorkflowTest() {
    super(server);
  }

  private static CreateWorkflowInstanceRequest req;
  private static CreateWorkflowInstanceResponse resp;

  @Test
  public void t01_createCreditApplicationWorkflow() {
    req = new CreateWorkflowInstanceRequest();
    req.type = "creditApplicationProcess";
    req.startState = CreditApplicationWorkflow.State.previewCreditApplication.toString();
    req.businessKey = UUID.randomUUID().toString();
    req.requestData = (new ObjectMapper()).valueToTree(
            new CreditApplicationWorkflow.CreditApplication("CUST123", new BigDecimal(100l)));
    req.externalId = UUID.randomUUID().toString();
    resp = fromClient(workflowInstanceResource, true).put(req, CreateWorkflowInstanceResponse.class);
    assertThat(resp.id, notNullValue());
  }

  @Test(timeout = 5000)
  public void t02_checkAcceptCreditApplicationReached() throws InterruptedException {
    ListWorkflowInstanceResponse response;
    do {
      response = getWorkflowInstance(resp.id, "acceptCreditApplication");
    } while (response.nextActivation != null);
  }

  @Test
  public void t03_moveToGrantLoanState() {
    UpdateWorkflowInstanceRequest ureq = new UpdateWorkflowInstanceRequest();
    ureq.nextActivationTime = now();
    ureq.state = "grantLoan";
    fromClient(workflowInstanceResource, true).path(resp.id).put(ureq);
  }

  @Test(timeout = 5000)
  public void t04_checkDoneStateReached() throws InterruptedException {
    ListWorkflowInstanceResponse response;
    do {
      response = getWorkflowInstance(resp.id, "done");
    } while (response.nextActivation != null);
  }

  @Test
  public void t05_checkWorkflowInstanceActions() {
    assertWorkflowInstance(resp.id, actionHistoryValidator(asList(
            new Action("previewCreditApplication", "", 0, null, null),
            new Action("acceptCreditApplication", "", 0, null, null), // probably not the way to show manual action in future
            new Action("acceptCreditApplication", "", 0, null, null),
            new Action("grantLoan", "", 0, null, null),
            new Action("finishCreditApplication", "", 0, null, null),
            new Action("done", "", 0, null, null))));
  }

}
