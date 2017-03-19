package cz.vondr.kiwi;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public final class CityName {
    final byte b1;
    final byte b2;
    final byte b3;

    public CityName(byte b1, byte b2, byte b3) {
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        CityName other = (CityName) o;
        return !(this.b1 != other.b1 ||
                this.b2 != other.b2 ||
                this.b3 != other.b3);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + b1;
        result = 31 * result + b2;
        result = 31 * result + b3;
        return result;
    }

    @Override
    public String toString() {
        byte[] bytes = {b1, b2, b3};
        return new String(bytes, ISO_8859_1);
    }
}
