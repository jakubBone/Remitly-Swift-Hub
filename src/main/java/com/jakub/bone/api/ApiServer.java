package com.jakub.bone.api;

import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.utills.ConfigLoader;
import com.jakub.bone.utills.FileImporter;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Log4j2
public class ApiServer {
    public static void main(String[] args) throws SQLException, IOException {
        Datasource datasource = new Datasource();

        // Import file with SWIFT data
        String file_path = ConfigLoader.get("database.swift_codes");
        List<SwiftRecord> swiftCodeRecords = FileImporter.importExcelFile(file_path);
        datasource.getCodeRepository().insertSwiftRecords(swiftCodeRecords);

        // Init Server
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler();
        server.setHandler(context);
        context.setAttribute("datasource", datasource);

        // Init Servlets
        context.addServlet(new ServletHolder(new SwiftCodeServlet()), "/v1/swift-codes/*");
        context.addServlet(new ServletHolder(new CountrySwiftCodeServlet()), "/v1/swift-codes/country/*");

        try {
            server.start();
            log.info("Server is running on http://localhost:8080");
            try {
                server.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.error("API Server interrupted", ex);
            }
        } catch (Exception ex) {
            log.error("Failed to to start API Server. Error: {}", ex.getMessage(), ex);
        } finally {
            try {
                server.stop();
                datasource.getDatabaseSchema().truncateTable();
            } catch (Exception ex) {
                log.error("Failed to stop API Server. Error: {}", ex.getMessage(), ex);
            }
        }
    }
}
