/**
 * 
 */
package ibg.cc.mq.writers;

/**
 * @author Gregory W. Arnold
 *
 */
import java.text.*;

/** StringPadder provides left pad and right pad functionality for Strings
 */
public  class StringPadder
{
   /** method to left pad a string with a given string to a given size. This
    *  method will repeat the padder string as many times as is necessary until
    *  the exact specified size is reached. If the specified size is less than the size of
    *  the original string then the original string is returned unchanged.
    *  Example1 - original string "cat", padder string "white", size 8 gives "whitecat".
    *  Example2 - original string "cat", padder string "white", size 15 gives "whitewhitewhcat".
    *  Example3 - original string "cat", padder string "white", size 2 gives "cat".
    *  @return String the newly padded string
    *  @param  stringToPad The original string
    *  @param  padder The string to pad onto the original string
    *  @param  size The required size of the new string
    */
   public static String leftPad (String stringToPad, String padder, int size)
   {
      String leftPad;
      StringBuffer strb_dyn;
      StringCharacterIterator sci_dyn;
      if (padder.length() == 0)
      {
         return stringToPad;
      }
      // MMISE 05/04/2009, fix null pointer exception
      if (stringToPad == null) 
      {
         stringToPad = "";
      }
      strb_dyn = new StringBuffer(size);
      sci_dyn  = new StringCharacterIterator(padder);
 
      while (strb_dyn.length() < (size - stringToPad.length()))
      {
         for (char ch = sci_dyn.first(); ch != CharacterIterator.DONE ; ch = sci_dyn.next())
         {
            if (strb_dyn.length() <  size - stringToPad.length())
            {
               strb_dyn.insert(strb_dyn.length(),String.valueOf(ch));
            }
         }
      }
      leftPad = strb_dyn.append(stringToPad).toString();
      
      return leftPad;
   }
   
   /** method to right pad a string with a given string to a given size. This
   *  method will repeat the padder string as many times as is necessary until
   *  the exact specified size is reached. If the specified size is less than the size of
   *  the original string then the original string is returned unchanged.
   *  Example1 - original string "cat", padder string "white", size 8 gives "catwhite".
   *  Example2 - original string "cat", padder string "white", size 15 gives "catwhitewhitewh".
   *  Example3 - original string "cat", padder string "white", size 2 gives "cat".
   *  @return String the newly padded string
   *  @param  stringToPad The original string
   *  @param  padder The string to pad onto the original string
   *  @param  size The required size of the new string
   */
   public static String rightPad (String stringToPad, String padder, int size,boolean tailTrim)
   {
      StringBuilder strb_dyn = null;
      StringCharacterIterator sci_dyn;

      try
      {
         if (padder.length() == 0)
         {
            return stringToPad;
         }
         // MMISE 05/04/2009, fix null pointer exception
         stringToPad = (stringToPad == null) ? "" : stringToPad;
         
         // trimming if the length of the stringToPad value is longer than the size
         if(tailTrim && stringToPad.length() > size)
         {
            return (stringToPad.substring(0,size));
         }
         
         strb_dyn = new StringBuilder(stringToPad);
         sci_dyn  = new StringCharacterIterator(padder);
    
         while (strb_dyn.length() < size)
         {
            for (char ch = sci_dyn.first(); ch != CharacterIterator.DONE ; ch = sci_dyn.next())
            {
               if (strb_dyn.length() < size)
               {
                  strb_dyn.append(String.valueOf(ch));
               }
            }
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
      return strb_dyn != null ? strb_dyn.toString() : stringToPad;
   }

  /**
   * <B>replicate</B>(<B>char</B> <i>strCharacter</i>, <B>int</B> <i>intLength</i>)<BR>
   * This method will replicate a character the specified number of times.
   * Call it by passing the character to replicate and the number of
   * times to replicate the character.<BR>
   * <B>Example:</B> replicate("M", 7) returns "MMMMMMM"<BR>
   * 
   * @param strCharacter - The character to replicate
   * @param intLength - The number of times to replicate the value
   * @return - String
   * @author - Mark Mise
   */
	public static String replicate(char strCharacter, int intLength)
	{
		  String str = "";
		  for(int i = 0; i < intLength; i++)
		  {
		    str += strCharacter;
		  }
		  return str;
	}
}
 