
import java.io.Serializable;

public class Payload implements Serializable{
	/**
	 * When an I/O stream manages 8-bit bytes of raw binary data, it is called a byte stream
	 * when the I/O stream manages 16-bit Unicode characters, it is called a character stream
	 *serialize means an object is converted to a byte stream so that byte stream could be converted back into copy of the object
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
					getPayloadType().toString(),  getMessage());
	}
}
