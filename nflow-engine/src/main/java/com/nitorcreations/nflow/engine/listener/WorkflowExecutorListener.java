package com.nitorcreations.nflow.engine.listener;

import static org.joda.time.DateTime.now;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.nitorcreations.nflow.engine.workflow.definition.NextAction;
import com.nitorcreations.nflow.engine.workflow.definition.StateExecution;
import com.nitorcreations.nflow.engine.workflow.definition.WorkflowDefinition;
import com.nitorcreations.nflow.engine.workflow.instance.WorkflowInstance;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * WorkflowExecutorListener is a global, stateless listener for workflow
 * executors.
 * <p>
 * Same instance of WorkflowExecutorListener is used for all workflow
 * state executions: all state must be stored in <code>ListenerContext.data</code>.
 * </p>
 */
public interface WorkflowExecutorListener {

  /**
   * ListenerContext instance is created at start of workflow state execution and passed to listener's
   * life-cycle methods.
   */
  @SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", justification = "listeners are implemented by business applications")
  public class ListenerContext {

    /**
     * The time when the listener context was created.
     */
    public final DateTime start = now();

    /**
     * The definition of the workflow.
     */
    public final WorkflowDefinition<?> definition;

    /**
     * The name of the state of the workflow instance before processing.
     */
    public final String originalState;

    /**
     * The workflow instance.
     */
    public final WorkflowInstance instance;

    /**
     * The access point for the workflow instance-specific information.
     */
    public final StateExecution stateExecution;

    /**
     * The action to be taken after workflow state execution. Available in
     * afterProcessing and afterFailure stages only. Changing the value in the
     * listener has no effect.
     */
    public NextAction nextAction = null;

    /**
     * Stateless listeners can use data to pass information between listener
     * stages.
     */
    public final Map<Object, Object> data = new LinkedHashMap<>();

    public ListenerContext(WorkflowDefinition<?> definition, WorkflowInstance instance, StateExecution stateExecution) {
      this.definition = definition;
      this.instance = instance;
      this.stateExecution = stateExecution;
      this.originalState = instance.state;
    }
  }

  /**
   * Executed before state is processed. Exceptions are logged but they do not
   * affect workflow processing.
   * @param listenerContext The listener context.
   */
  void beforeProcessing(ListenerContext listenerContext);

  /**
   * Executed after state has been successfully processed and before persisting
   * state. Exceptions are logged but they do not affect workflow processing.
   * @param listenerContext The listener context.
   */
  void afterProcessing(ListenerContext listenerContext);

  /**
   * Executed after state processing has failed and before persisting state.
   * Exceptions are logged but they do not affect workflow processing.
   * @param listenerContext The listener context.
   * @param throwable The exception thrown by the state handler method.
   */
  void afterFailure(ListenerContext listenerContext, Throwable throwable);
}
