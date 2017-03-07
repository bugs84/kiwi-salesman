package cz.vondr.kiwi.solutionwriter;

import java.io.IOException;

class WriteInTwoAppendables implements Appendable {
    private Appendable a1;
    private Appendable a2;

    public WriteInTwoAppendables(Appendable a1, Appendable a2) {
        this.a1 = a1;
        this.a2 = a2;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        a1.append(csq);
        a2.append(csq);
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        a1.append(csq, start, end);
        a2.append(csq, start, end);
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
        a1.append(c);
        a2.append(c);
        return this;
    }
}
