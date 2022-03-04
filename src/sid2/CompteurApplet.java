package sid2;

import javacard.framework.*;

/**
 *
 * @author Hasimbola
 */
public class CompteurApplet extends Applet {
    
  final static byte Wallet_CLA =(byte)0xB0;
     // codes of INS byte in the command APDU header
  final static byte INIT = (byte) 0x10;
  final static byte VERIFY = (byte) 0x20;
  final static byte CREDIT = (byte) 0x30;
  final static byte DEBIT = (byte) 0x40;
  final static byte GET_BALANCE = (byte) 0x50;
  final static byte UNBLOCK = (byte) 0x60;
  final static byte CHANGE_PIN = (byte) 0x70;
  final static byte SET_DATE = (byte)0x80;
  final static byte GET_DATE = (byte)0x90;
  final static byte SET_NUM = (byte)0x11;
  final static byte GET_NUM = (byte)0x12;
  private final static byte INS_SET_FINGERPRINT = (byte) 0x07;
  private final static byte INS_GET_FINGERPRINT = (byte) 0x08;
  
  // maximum balance
  
  final static short MAX_BALANCE = (short) 0x7FFF;
  
  // maximum transaction amount 
  final static short MAX_TRANSACTION_AMOUNT = (short) 0xCB;
  
  
  // maximum number of incorrect tries before the
  // PIN is blocked
  final static byte PIN_TRY_LIMIT =(byte)0x03;
  
  // maximum size PIN
  final static byte MAX_PIN_SIZE =(byte)0x08;
  
  //Minimum PIN size
  final static byte MIN_PIN_SIZE =(byte)0x4;
  
  // minimum data size
  final static byte MIN_DATA_SIZE = (byte)0x00;
  
  //minimum data size
  final static byte OFF_DATA = (byte)0x02;
  
  // minimum data size
  final static byte MIN_DATE_SIZE = (byte)0x00;
  
  //minimum data size
  final static byte OFF_DATE = (byte)0x02;
  
  // minimum data size
  final static byte MIN_COMPTE_SIZE = (byte)0x00;
  
  //minimum data size
  final static byte OFF_COMPTE = (byte)0x02;
  
  // signal that the PIN verification failed
  final static short SW_VERIFICATION_FAILED = 0x6312;
   
  // signal the PIN validation is required
  // for a credit or a debit transaction
  final static short SW_PIN_VERIFICATION_REQUIRED = 0x6311;
   
  // signal invalid transaction amount
  // amount > MAX_TRANSACTION_MAOUNT or amount < 0
  final static short SW_INVALID_TRANSACTION_AMOUNT = 0x6A83;
  
  //For change the PIN
  private static final byte TMP_SIZE = (byte)0x08;
   
  // signal that the balance exceed the maximum
  final static short SW_EXCEED_MAXIMUM_BALANCE = 0x6A84;
   
  // signal the balance becomes negative
  final static short SW_NEGATIVE_BALANCE = 0x6A85;
  
  /* instance variables declaration */
  OwnerPIN pin;
  short balance;
  byte[] tmp;
  private byte[] date;
  private byte[] num;
  private byte[] compte;
  private byte[] fingerPrint;
  
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new CompteurApplet(bArray, bOffset, bLength);
    }

    protected CompteurApplet(byte[] bArray, short bOffset, byte bLength) {

    pin = new OwnerPIN(PIN_TRY_LIMIT, MAX_PIN_SIZE);
	compte = new byte[] { };
	date = new byte[] { };    	
	num = new byte[] { };
    fingerPrint = new byte[] { };
    
    tmp = JCSystem.makeTransientByteArray(TMP_SIZE,  JCSystem.CLEAR_ON_DESELECT);

    	byte [] pinArr= {1,2,3,4,5,6};
    	pin.update(pinArr, (short)0, (byte)pinArr.length);
	    register();
    }

   public boolean select() {
    if ( pin.getTriesRemaining() == 0 ) return false;
    return true;
  }// end of select method
   
   public void deselect() {
    pin.reset();
  }
   
    public void process(APDU apdu) {
    	if(selectingApplet()) {
    		return;
    	}
    byte[] buffer = apdu.getBuffer();
    
    if ((buffer[ISO7816.OFFSET_CLA] == 0) &&
        (buffer[ISO7816.OFFSET_INS] == (byte)(0xA4))) return;
    if (buffer[ISO7816.OFFSET_CLA] != Wallet_CLA)
      ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
    
    switch (buffer[ISO7816.OFFSET_INS]) {
      case GET_BALANCE: getBalance(apdu);
        return;
      case DEBIT: debit(apdu);
        return;
      case CREDIT: credit(apdu);
        return;
      case INIT: init(apdu);
        return;
      case SET_DATE: setDate(apdu);
    	return;
      case GET_DATE: getDate(apdu);
    	return;
      case SET_NUM: setNum(apdu);
	  	return;
      case GET_NUM: getNum(apdu);
	  	return;
      case VERIFY: verify(apdu);
      	return;
      case CHANGE_PIN: changePin(apdu);
    	return;
      case INS_GET_FINGERPRINT:getFingerPrint(apdu);
    	return;
      case INS_SET_FINGERPRINT:setFingerPrint(apdu); 
    	return;
      case UNBLOCK: pin.resetAndUnblock();
        return;
      default: ISOException.throwIt (ISO7816.SW_INS_NOT_SUPPORTED);
    }
  } // end of process method
    
    private void credit(APDU apdu) {
    // access authentication
    if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED); 
    byte[] buffer = apdu.getBuffer();
	 apdu.setIncomingAndReceive();
     compte = Utils.getDataFromBuffer(buffer, ISO7816.OFFSET_CDATA, apdu.getIncomingLength());
  
  } // end of deposit method
    
    private void debit(APDU apdu) {
	    // access authentication
	    if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
	    byte[] buffer = apdu.getBuffer();
	    apdu.setIncomingAndReceive();
        compte = Utils.getDataFromBuffer(buffer, ISO7816.OFFSET_CDATA, apdu.getIncomingLength());
  } // end of debit method
    
    private void init(APDU apdu) {
    	// access authentication
	    if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);	
	    byte[] buffer = apdu.getBuffer();
	    apdu.setIncomingAndReceive();
        compte = Utils.getDataFromBuffer(buffer, ISO7816.OFFSET_CDATA, apdu.getIncomingLength());
      } // end of init method
    
    
    private void getBalance(APDU apdu) {
        if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);	
        byte[] buffer = apdu.getBuffer();
  	  short i = 0;
        for(i = 0; i < compte.length; i++) {
            buffer[i] = compte[i];
        }
        short finalLength = (short) (compte.length);
        
        apdu.setOutgoingAndSend((short)0, finalLength);
  } // end of getBalance method
    
  private void setDate(APDU apdu) {
	  if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
	  byte[] buffer = apdu.getBuffer();
	  apdu.setIncomingAndReceive();
      date = Utils.getDataFromBuffer(buffer, ISO7816.OFFSET_CDATA, apdu.getIncomingLength());
  }
  
  private void getDate(APDU apdu) {
      if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);	
      byte[] buffer = apdu.getBuffer();
	  short i = 0;
      for(i = 0; i < date.length; i++) {
          buffer[i] = date[i];
      }
      short finalLength = (short) (date.length + 3);
      apdu.setOutgoingAndSend((short)0, finalLength);
  }
    
    private void verify(APDU apdu) {
	    byte[] buffer = apdu.getBuffer();
	    byte byteRead = (byte)(apdu.setIncomingAndReceive());
	    if (! pin.check(buffer, ISO7816.OFFSET_CDATA,byteRead)) {
	    		ISOException.throwIt((short) ((SW_VERIFICATION_FAILED) | pin.getTriesRemaining()));
	    	}
    }
    
    private short unsigned(byte b) {
    	return (short) (b & 0x00FF);
    }
    
    private void changePin(APDU apdu) {
    	 byte[] buf = apdu.getBuffer();
    	 byte byteRead = (byte)(apdu.setIncomingAndReceive());
	 	 pin.update(buf, ISO7816.OFFSET_CDATA,byteRead);
	 	 pin.getTriesRemaining();
	 	 pin.resetAndUnblock();    
    }
    
    private void setNum(APDU apdu) {
    	if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
	   	byte[] buffer = apdu.getBuffer();
	    apdu.setIncomingAndReceive();
        num = Utils.getDataFromBuffer(buffer, ISO7816.OFFSET_CDATA, apdu.getIncomingLength());
    }
    
    private void getNum(APDU apdu) {
      if ( ! pin.isValidated()) ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);	
      byte[] buffer = apdu.getBuffer();
	  short i = 0;
      for(i = 0; i < num.length; i++) {
          buffer[i] = num[i];
      }
      short finalLength = (short) (num.length + 3);
      apdu.setOutgoingAndSend((short)0, finalLength);
    }
    
    private void setFingerPrint(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
    	 apdu.setIncomingAndReceive();
         
         if (buffer[ISO7816.OFFSET_P1] == 0x00 && buffer[ISO7816.OFFSET_P2] == 0x00) {
             short dataLength = Utils.byteArrayDataToNumber(buffer, ISO7816.OFFSET_CDATA, apdu.getIncomingLength());
             fingerPrint = new byte[dataLength];
         } else {
             Util.arrayCopyNonAtomic(
                 buffer,
                 apdu.getOffsetCdata(),
                 fingerPrint,
                 (short) ((short) (buffer[ISO7816.OFFSET_P1] & 0xFF) * 100),
                 (byte) (buffer[ISO7816.OFFSET_P2] & 0xFF)
             );
         }
    }
    
    private void getFingerPrint(APDU apdu) {
    	  // Array copy: (src, offset, target, offset,copy size)
    	byte[] buffer = apdu.getBuffer();
        short p2 = (short)(buffer[ISO7816.OFFSET_P2] & 0xFF);
        short p1 = (short)(buffer[ISO7816.OFFSET_P1] & 0xFF);

        Util.arrayCopyNonAtomic(fingerPrint, (short) (p1 * 100), buffer, ISO7816.OFFSET_CDATA, p2);
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, p2);
    }
    
} // end of class Wallet
  