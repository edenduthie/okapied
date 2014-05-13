package okapied.action.json;

import java.util.List;

import okapied.entity.Feedback;
import okapied.service.FeedbackService;


public class FeedbackAction 
{
    private String LIST = "list";
    
    Integer propertyId;
    Boolean positive;
    Integer limit;
    Integer offset;
    Long size;
    List<Feedback> feedback;
    
    FeedbackService feedbackService;
    
    public void nullServices()
    {
    	feedbackService = null;
    }
    
    public String list()
    {
    	feedback = feedbackService.getFeedback(propertyId, positive, limit, offset);
    	size = feedbackService.getFeedbackSize(propertyId, positive);
    	nullServices();
    	return LIST;
    }

	public Integer getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	public Boolean getPositive() {
		return positive;
	}

	public void setPositive(Boolean positive) {
		this.positive = positive;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
	}

	public void setFeedbackService(FeedbackService feedbackService) {
		this.feedbackService = feedbackService;
	}

	public List<Feedback> getFeedback() {
		return feedback;
	}

	public void setFeedback(List<Feedback> feedback) {
		this.feedback = feedback;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}
}
