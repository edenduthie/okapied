package okapied.action.app;

public class Av extends ListBaseAction implements PageAction
{

	@Override
	public int getPage() {
		return 5;
	}
	
	public String getTitle()
	{
		return "Availability and Pricing";
	}
	
	public String getDescription()
	{
		return "Completely manage the availability and prices of your place. The calendar is instantly updated.";
	}

}
