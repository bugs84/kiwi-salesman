package cz.vondr.kiwi;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public final class CityName {
    final byte[] bytes;

    /** cityNameBytes nesmi byt null a musi mit length==3 */
    public CityName(byte[] cityNameBytes) {
        this.bytes = Arrays.copyOf(cityNameBytes, cityNameBytes.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
        byte[] bytes2 = ((CityName) o).bytes;
        return !(bytes[0] != bytes2[0] ||
                bytes[1] != bytes2[1] ||
                bytes[2] != bytes2[2]);

        //before simplify
//        if (bytes[0] != bytes2[0] ||
//                        bytes[1] != bytes2[1] ||
//                        bytes[2] != bytes2[2]) {
//                    return false;
//                }
//                return true;

//        return Arrays.equals(bytes, ((CityName) o).bytes);
    }

    @Override
    public int hashCode() {
        //TODO se da rozbalit neni potreba kontrolovat null
        int result = 1;
        result= 31 * result + bytes[0];
        result= 31 * result + bytes[1];
        result= 31 * result + bytes[2];
        return result;


//        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return new String(bytes, ISO_8859_1);
    }
}
