package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class ServerInfoDAO implements IServerInfoDao {

    private static final Logger logger = Logger.getLogger(ServerInfoDAO.class.getName());

    @Override
    public List<ServerInfo> getServerInfo() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        String query = "SELECT * FROM serverinfo";
        List<ServerInfo> serverInfoList = session.createNativeQuery(query, ServerInfo.class).list();

        session.close();

        if (serverInfoList.isEmpty()) {
            logger.log(Level.INFO, "No server info logs found.");
            return Collections.emptyList();
        }

        return serverInfoList;
    }
}
