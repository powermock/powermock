/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samples.classwithinnermembers;

/**
 * Class that is used to test that local and member class works with PowerMock.
 */
public class ClassWithInnerMembers {

    private interface InnerInterface {
        String doStuff();
    }

    public static class MyInnerClassWithPrivateConstructorWithDiffMultArgs {

        private String mArg1;
        private int mArg2;
        private String mArg3;

        private MyInnerClassWithPrivateConstructorWithDiffMultArgs(String arg1, int arg2, String arg3) {
            mArg1 = arg1;
            mArg2 = arg2;
            mArg3 = arg3;
        }
    }

    public MyInnerClassWithPrivateConstructorWithDiffMultArgs makeMyInnerClassWithPrivateConstructorWithDiffMultArgs(
            String arg1, int arg2, String arg3) {
        return new MyInnerClassWithPrivateConstructorWithDiffMultArgs(arg1, arg2, arg3);
    }

    public static class MyInnerClassWithPrivateConstructorWithMultArgs {

        private String mArg1;
        private String mArg2;
        private String mArg3;

        private MyInnerClassWithPrivateConstructorWithMultArgs(String arg1, String arg2, String arg3) {
            mArg1 = arg1;
            mArg2 = arg2;
            mArg3 = arg3;
        }
    }

    public MyInnerClassWithPrivateConstructorWithMultArgs makeMyInnerClassWithPrivateConstructorWithMultArgs(
            String arg1, String arg2, String arg3) {
        return new MyInnerClassWithPrivateConstructorWithMultArgs(arg1, arg2, arg3);
    }

    public static class MyInnerClassWithPublicConstructorWithMultArgs {

        private String mArg1;
        private String mArg2;
        private String mArg3;

        private MyInnerClassWithPublicConstructorWithMultArgs(String arg1, String arg2, String arg3) {
            mArg1 = arg1;
            mArg2 = arg2;
            mArg3 = arg3;
        }
    }

    public MyInnerClassWithPublicConstructorWithMultArgs makeMyInnerClassWithPublicConstructorWithMultArgs(
            String arg1, String arg2, String arg3) {
        return new MyInnerClassWithPublicConstructorWithMultArgs(arg1, arg2, arg3);
    }

    public static class MyInnerClassWithPackageConstructorWithMultArgs {

        private String mArg1;
        private String mArg2;
        private String mArg3;

        MyInnerClassWithPackageConstructorWithMultArgs(String arg1, String arg2, String arg3) {
            mArg1 = arg1;
            mArg2 = arg2;
            mArg3 = arg3;
        }
    }

    public MyInnerClassWithPackageConstructorWithMultArgs makeMyInnerClassWithPackageConstructorWithMultArgs(
            String arg1, String arg2, String arg3) {
        return new MyInnerClassWithPackageConstructorWithMultArgs(arg1, arg2, arg3);
    }

    public static class MyInnerClassWithProtectedConstructorWithMultArgs {

        private String mArg1;
        private String mArg2;
        private String mArg3;

        MyInnerClassWithProtectedConstructorWithMultArgs(String arg1, String arg2, String arg3) {
            mArg1 = arg1;
            mArg2 = arg2;
            mArg3 = arg3;
        }
    }

    public MyInnerClassWithProtectedConstructorWithMultArgs makeMyInnerClassWithProtectedConstructorWithMultArgs(
            String arg1, String arg2, String arg3) {
        return new MyInnerClassWithProtectedConstructorWithMultArgs(arg1, arg2, arg3);
    }

    private static class MyInnerClass implements InnerInterface {

        @Override
        public String doStuff() {
            return "member class";
        }
    }

    private static class StaticInnerClassWithConstructorArgument implements InnerInterface {

        private final String value;

        public StaticInnerClassWithConstructorArgument(String value) {
            this.value = value;
        }

        @Override
        public String doStuff() {
            return value;
        }
    }

    private class MyInnerClassWithConstructorArgument implements InnerInterface {

        private final String value;

        public MyInnerClassWithConstructorArgument(String value) {
            this.value = value;
        }

        @Override
        public String doStuff() {
            return value;
        }
    }

    public String getValue() {
        return new MyInnerClass().doStuff();
    }

    public String getValueForInnerClassWithConstructorArgument() {
        return new MyInnerClassWithConstructorArgument("value").doStuff();
    }

    public String getValueForStaticInnerClassWithConstructorArgument() {
        return new StaticInnerClassWithConstructorArgument("value").doStuff();
    }

    public String getLocalClassValue() {
        class MyLocalClass implements InnerInterface {
            @Override
            public String doStuff() {
                return "local class";
            }
        }

        return new MyLocalClass().doStuff();
    }

    public String getLocalClassValueWithArgument() {
        class MyLocalClass implements InnerInterface {

            private final String value;

            public MyLocalClass(String value) {
                this.value = value;
            }

            @Override
            public String doStuff() {
                return value;
            }
        }

        return new MyLocalClass("my value").doStuff();
    }

    public String getValueForAnonymousInnerClass() {

        InnerInterface inner = new InnerInterface() {

            @Override
            public String doStuff() {
                return "value";
            }
        };

        return inner.doStuff();
    }
}
