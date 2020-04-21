
import java.io.Serializable;
public class Payload implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6625037986217386003L;
	private String message;

	public void setMessage(String s) {
		this.message = s;
	}
	public String getMessage() {
		return this.message;
	}
	
	private PayloadType payloadType;
	public void setPayloadType(PayloadType pt) {
		this.payloadType = pt;
	}
	public PayloadType getPayloadType() {
		return this.payloadType;
	}
	

	@Override
	public String toString() {
		return String.format("Type[%s], Message[%s]",
					getPayloadType().toString(), getMessage());
	}
}