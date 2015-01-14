package hk.window;

public class Title
{
	public String getTitle() 
	{
		return isEdited ? "*" + title : title;
	}
	
	public void setTitle(String filename) 
	{
		this.filename = filename;
		this.title = this.filename + " - Percolation App";
	}
	
	public boolean isEdited() 
	{
		return isEdited;
	}
	
	public void setEdited(boolean isEdited) 
	{
		this.isEdited = isEdited;
	}
	
	public String getFilename() 
	{
		return filename;
	}

	@Override
	public String toString()
	{
		return this.getTitle();
	}

	private String title = "Percolation App";
    private String filename = "";
    private boolean isEdited = false;
}
