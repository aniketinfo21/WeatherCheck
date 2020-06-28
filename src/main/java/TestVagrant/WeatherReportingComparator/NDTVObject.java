package TestVagrant.WeatherReportingComparator;

public class NDTVObject {
	public String condition;
	public String wind;
	public String humidity;
	public String tempInD;
	public String tempInF;

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public void setTempInDegree(String tempInD) {
		this.tempInD = tempInD;
	}

	public void setTempInFahrenheit(String tempInF) {
		this.tempInF = tempInF;
	}
	
	public boolean isEmpty() {
		if(condition.equals(null) && wind.equals(null) && humidity.equals(null) && tempInD.equals(null) && tempInF.equals(null)) {
			return true;
		}		
		return false;
	}

}
