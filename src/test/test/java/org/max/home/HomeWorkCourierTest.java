package java.org.max.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HomeWorkCourierTest extends AbstractTest {
    @Test
    @Order(1)
    void addCourierToListTest() {
        Session session = getSession();
        final Query query = session.createSQLQuery("SELECT MAX(courier_id) FROM courier_info");
        int lastId = (int) query.uniqueResult() + 1;

        CourierInfoEntity newCourier = new CourierInfoEntity();
        newCourier.setCourierId((short) lastId);
        newCourier.setFirstName("Anton");
        newCourier.setLastName("Ivanov");
        newCourier.setPhoneNumber("+ 7 100743 0206");
        newCourier.setDeliveryType("car");

        session.beginTransaction();
        session.persist(newCourier);
        session.getTransaction().commit();

        final Query query1 = session.createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + lastId)
                .addEntity(CourierInfoEntity.class);
        CourierInfoEntity addedCourier = (CourierInfoEntity) query1.uniqueResult();
        //then
        Assertions.assertNotNull(addedCourier);
        Assertions.assertEquals("Anton", addedCourier.getFirstName());
        Assertions.assertEquals("Ivanov", addedCourier.getLastName());
        Assertions.assertEquals("+ 7 100743 0206", addedCourier.getPhoneNumber());
        Assertions.assertEquals("car", addedCourier.getDeliveryType());
    }

    @Test
    @Order(2)
    void summaOfCouriersTest() {
        String sql = "FROM CourierInfoEntity";
        final Query query = getSession().createQuery(sql);
        //then
        Assertions.assertEquals(4, query.list().size());
    }

    @Test
    @Order(3)
    void summaOfCouriersWithoutCarTest() throws SQLException {
        //given
        String sql = "SELECT * FROM courier_info WHERE delivery_type='foot'";
        Statement stmt = getConnection().createStatement();
        int countCouriers = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countCouriers++;
        }
        //then
        Assertions.assertEquals(1, countCouriers);
    }

    @Test
    @Order(4)
    void summaOfCouriersWithCarTest() throws SQLException {
        //given
        String sql = "SELECT * FROM courier_info WHERE delivery_type='car'";
        Statement stmt = getConnection().createStatement();
        int countCouriers = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countCouriers++;
        }
        //then
        Assertions.assertEquals(3, countCouriers);
    }


    @Test
    @Order(5)
    void deleteCourierFromListTest() {
        Session session = getSession();
        final Query query = session.createSQLQuery("SELECT * FROM courier_info WHERE last_name='Black'")
                .addEntity(CourierInfoEntity.class);
        CourierInfoEntity courierForDelete = (CourierInfoEntity) query.uniqueResult();

        Assumptions.assumeTrue(courierForDelete != null);

        session.beginTransaction();
        session.delete(courierForDelete);
        session.getTransaction().commit();

        final Query query1 = session.createSQLQuery("SELECT * FROM courier_info WHERE phone_number='+ 7 100743 0206'")
                .addEntity(CourierInfoEntity.class);

        CourierInfoEntity courierAfterDelete = (CourierInfoEntity) query1.uniqueResult();

        Assertions.assertNull(courierAfterDelete);
    }
}