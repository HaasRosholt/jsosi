package no.jsosi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class SosiReaderTest extends TestCase {

    public void testAddress() throws IOException {
        File file = new File("src/test/resources/0219Adresser.SOS");
        assertTrue(file.canRead());

        SosiReader r = new SosiReader(new FileInputStream(file));
        assertEquals("EPSG:25833", r.getCrs());

        List<Feature> features = new ArrayList<Feature>();
        Feature feature;
        while ((feature = r.nextFeature()) != null) {
            features.add(feature);
        }

        assertEquals(33545, features.size());

        Feature f = features.get(0);
        assertEquals("Hans Hanssens vei", f.get("GATENAVN"));
        assertEquals("SNARØYA", f.get("POSTNAVN"));
        assertEquals("0219", f.get("KOMM"));
        assertNull(f.get("NØ"));
        assertNotNull(f.getGeometry());
        assertTrue(f.getGeometry() instanceof Point);
        assertEquals(1, f.getCoordinateCount());

        r.close();

    }

    public void testVbase() throws IOException {
        File file = new File("src/test/resources/Vbase_02.SOS");
        assertTrue(file.canRead());

        SosiReader r = new SosiReader(new FileInputStream(file));
        assertEquals("EPSG:25833", r.getCrs());

        Feature f1 = r.nextFeature();
        assertEquals("P V 99834", f1.get("VNR"));
        assertNull(f1.get("GATENAVN"));
        assertEquals(12, f1.getCoordinateCount());
        assertTrue(f1.getGeometry() instanceof LineString);

        Feature f2 = r.nextFeature();
        assertEquals("Åsveien", f2.get("GATENAVN"));
        assertEquals(15, f2.getCoordinateCount());
        assertTrue(f2.getGeometry() instanceof LineString);

        int count = 1;
        Feature f = null;
        while ((f = r.nextFeature()) != null) {
            count++;
            assertNotNull(f.getGeometry());
        }

        assertTrue(count > 10000);

        r.close();

    }
    
    public void testArealdekke() throws IOException {
        File file = new File("src/test/resources/1421_Arealdekke.sos");
        assertTrue(file.canRead());

        SosiReader r = new SosiReader(new FileInputStream(file));
        assertEquals("EPSG:25832", r.getCrs());
        
        Feature f1 = r.nextFeature();
        assertEquals("10000101", f1.get("OPPDATERINGSDATO"));
        assertEquals("ÅpentOmråde", f1.get("OBJTYPE"));
        assertNull(f1.get("GATENAVN"));
        assertTrue(f1.getGeometry() instanceof Polygon);
        assertTrue(f1.getGeometry().isValid());
        
        Feature f5763 = r.getFeature(5763);
        assertEquals(5763, f5763.getId().intValue());
        assertEquals("Innsjø", f5763.get("OBJTYPE"));
        assertTrue(f5763.getGeometry() instanceof Polygon);
        assertTrue(f5763.getGeometry().isValid());

        r.close();
    }

}