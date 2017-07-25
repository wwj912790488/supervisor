package com.arcsoft.supervisor.agent.service.task.support;

import com.arcsoft.supervisor.agent.service.task.StartTaskException;
import com.arcsoft.supervisor.agent.service.task.TranscoderXmlUtils;
import com.arcsoft.supervisor.agent.service.task.TranscodingTrackerResource;
import com.arcsoft.supervisor.agent.service.task.resource.ComposeTaskTranscodingTrackerResource;
import com.arcsoft.supervisor.agent.service.task.resource.TaskResourceHolder;
import com.arcsoft.supervisor.model.vo.task.AbstractTaskParams;
import com.arcsoft.supervisor.transcoder.ITranscodingTracker;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;

/**
 * This class providers extra method on class <code>AbstractTaskProcessorSupport</code>.
 *
 * @author zw.
 */
public abstract class AbstractComposeStreamTaskProcessor extends AbstractTranscoderTaskProcessorSupport {


    /**
     * A class holds generated transcoder xml and model during lifecycle of task.
     */
    protected static final class TranscoderXmlAndTemplateModel {
        private final String transcoderXmlContent;
        private final Map<String, Object> model;

        public TranscoderXmlAndTemplateModel(String transcoderXmlContent, Map<String, Object> model) {
            this.transcoderXmlContent = transcoderXmlContent;
            this.model = model;
        }

        public String getTranscoderXmlContent() {
            return transcoderXmlContent;
        }

        public Map<String, Object> getModel() {
            return model;
        }
    }

    /**
     * Retrieves the xml of transcoder using {@link ComposeTaskTranscodingTrackerResource#templateModel}.
     *
     * @param tracker tracker contains template model
     * @param template the template of transcoder xml
     * @return completed xml use {@link ComposeTaskTranscodingTrackerResource#templateModel} as model.
     * @throws IOException
     * @throws TemplateException
     */
    protected String getTranscoderXmlBaseOnTracker(ITranscodingTracker tracker, String template) throws IOException, TemplateException {
        return TranscoderXmlUtils.generateTranscoderXmlFromTemplateString(
                template,
                getTranscodingTrackerResourceOfComposeTask(tracker).getResource().getTemplateModel()
        );
    }

    @SuppressWarnings("all")
    protected TranscodingTrackerResource<ComposeTaskTranscodingTrackerResource> getTranscodingTrackerResourceOfComposeTask(ITranscodingTracker tracker) {
        TaskResourceHolder holder = (TaskResourceHolder) tracker.getUserData();
        return holder.<ComposeTaskTranscodingTrackerResource>getByType(ComposeTaskTranscodingTrackerResource.class);
    }

    /**
     * Returns the {@link ITranscodingTracker} and use {@link TaskResourceHolder} as user data.
     *
     * @param taskParams the parameters of task
     * @return {@link ITranscodingTracker}
     * @throws StartTaskException if failed to create transcoder xml
     */
    protected ITranscodingTracker createTrackerAndSetUserData(AbstractTaskParams taskParams) throws StartTaskException {
        TranscoderXmlAndTemplateModel model;
        try {
            model = createTranscoderModel(taskParams);
        } catch (IOException | TemplateException e) {
            throw new StartTaskException(e);
        }

        TaskResourceHolder holder = new TaskResourceHolder();
        holder.addResource(getTranscodingTrackerResourceOfComposeTask(model, taskParams));

        ITranscodingTracker tracker = createITranscodingTracker(
                taskParams.getId(),
                model.getTranscoderXmlContent()
        );
        tracker.setUserData(holder);
        return tracker;
    }

    protected abstract ComposeTaskTranscodingTrackerResource getTranscodingTrackerResourceOfComposeTask(
            TranscoderXmlAndTemplateModel model, AbstractTaskParams taskParams);

    /**
     * Create {@code TranscoderXmlAndTemplateModel} with given {@code taskParams}.
     *
     * @param taskParams the parameters of task
     */
    public abstract TranscoderXmlAndTemplateModel createTranscoderModel(AbstractTaskParams taskParams) throws IOException, TemplateException;

    /**
     * Reload transcoder task base on a running task.
     *
     * @param taskParams the taskParams
     * @throws IOException
     * @throws TemplateException
     */
    public abstract void reload(AbstractTaskParams taskParams) throws IOException, TemplateException;

    /**
     * Display message on the screen of task.
     *
     * @param taskId   the identify value of task
     * @param taskType the type of task
     * @param message  the message to be display
     */
    public abstract void displayMessage(int taskId, int taskType, String message);

    /**
     * Set waring border in given <code>index</code> of task by <code>taskId</code>
     *
     * @param taskId the id of task
     * @param index the index of graphics
     * @param isShow <code>true</code> for show warning border otherwise is disable it
     */
    public abstract void warnBorder(int taskId, int index, boolean isShow);

    public abstract void displayStyledMessage(int taskId, String font, int size, int color, int alpha, int bgcolor, int bgalpha,
                                              int x, int y, int width, int height, String message);

}
