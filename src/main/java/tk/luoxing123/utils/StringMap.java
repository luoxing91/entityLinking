package tk.luoxing123.utils;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;
import org.apache.commons.lang3.StringUtils;

/* @param <T>*/
public class StringMap<T> extends TCustomHashMap<String,T> {
    private static final HashFunction defaultHashFunction
        = Hashing.murmur3_32();
    private static final HashingStrategy<String>
        stringHashStrat = new HashingStrategy<String>(){
        /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		@Override
        public boolean equals(String arg1,String arg2){
            return StringUtils.equals(arg1,arg2);
        }
        @Override
        public int computeHashCode(String str){
            return defaultHashFunction.hashUnencodedChars(str).asInt();
        }
        // TODO: 
    };
    private static final float  Load_Factor =0.97f;
    public StringMap(){
        super(stringHashStrat,10,Load_Factor);
    }
    public StringMap(int capacity){
        super(stringHashStrat,(int)Math.floor(capacity/Load_Factor),Load_Factor);
    }
}
