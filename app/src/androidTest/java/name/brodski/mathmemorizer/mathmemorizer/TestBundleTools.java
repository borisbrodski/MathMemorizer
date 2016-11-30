package name.brodski.mathmemorizer.mathmemorizer;

import android.app.Application;
import android.os.Bundle;
import android.test.ApplicationTestCase;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import icepick.State;
import name.brodski.mathmemorizer.mathmemorizer.tools.BundleTool;
import name.brodski.mathmemorizer.mathmemorizer.tools.Save;

/**
 * Created by boris on 29.11.16.
 */

public class TestBundleTools extends ApplicationTestCase<Application>  {
    static class C1 {
        @Save
        String s1;
        @Save
        private int i1;
        @Save
        private List<Long> ids = new ArrayList<>();

        public C1(String s1, int i1, List<Long> ids) {
            this.s1 = s1;
            this.i1 = i1;
            this.ids = ids;
        }

        public int getI1() {
            return i1;
        }

        public List<Long> getIds() {
            return ids;
        }

        public String getS1() {
            return s1;
        }
    }

    public TestBundleTools() {super(Application.class);}

//    @Test
//    public void saveC1() {
//        C1 c1 = new C1("xx", -23, Arrays.asList(323L, -423234L));
//        Bundle bundle = new Bundle();
//        BundleTool.save(c1, bundle, "my.");
//
//        C1 c12 = new C1("yy", -32222, Arrays.asList(-213L, 3L));
//        BundleTool.load(c12, bundle, "my.");
//
//        Assert.assertEquals(c1.getI1(), c12.getI1());
//        Assert.assertEquals(c1.getIds(), c12.getIds());
//        Assert.assertEquals(c1.getS1(), c12.getS1());
//    }
}
