class Seven {
    public static SeptenaryStringFunction fun = new SeptenaryStringFunction() {
        @Override
        public String apply(String... ss) {
            StringBuilder s = new StringBuilder();
            for (String next : ss) {
                s.append(next);
            }
            return s.toString().toUpperCase();
        }
    };
}


@FunctionalInterface
interface SeptenaryStringFunction {
    String apply(String... ss);
}
