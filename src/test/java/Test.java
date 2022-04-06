public class Test {

    public Test() {
        System.out.println(fun(2));
    }

    private int fun(int n) {
        int r = 0;
        int q = 8*n;
        for (int i=0; i<=q; i = i+4)
            for (int j=0; j<i; j++)
                r++;
        return r;
    }

}
