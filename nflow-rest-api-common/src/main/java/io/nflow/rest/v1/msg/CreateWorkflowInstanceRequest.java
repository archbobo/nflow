package io.nflow.rest.v1.msg;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.DateTime;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.nflow.engine.model.ModelObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description =
  "Request for submitting a new workflow instance. Note that if externalId is given, " +
  "type and externalId pair must be unique hence enabling retry-safety.")
@SuppressFBWarnings(value="URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification="jackson reads dto fields")
public class CreateWorkflowInstanceRequest extends ModelObject {

  @NotNull
  @Size(max=30)
  @ApiModelProperty(value = "Workflow definition type", required = true)
  public String type;

  @Size(max=64)
  @ApiModelProperty("Main business key or identifier for the new workflow instance")
  public String businessKey;

  @Size(max=64)
  @ApiModelProperty("Start state name (if other than default set in workflow definition)")
  public String startState;

  @Size(max=64)
  @ApiModelProperty("Unique external identifier within the workflow type. Generated by nflow if not given.")
  public String externalId;

  @ApiModelProperty("Start time for workflow execution. If null, defaults to now, unless activate is set to false, in which case activationTime is ignored.")
  public DateTime activationTime;

  @ApiModelProperty("Set to false to force activationTime to null. Default is true.")
  public Boolean activate;

  @ApiModelProperty("Create the workflow as a child of the given parent workflow.")
  public Long parentWorkflowId;

  @ApiModelProperty("State variables to be set for the new workflow instance.")
  public Map<String, Object> stateVariables = new HashMap<>();

}
