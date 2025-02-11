package com.jakub.bone.api;

import com.google.gson.Gson;
import com.jakub.bone.database.Datasource;
import com.jakub.bone.domain.SwiftRecord;
import com.jakub.bone.utills.SwiftMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/v1/swift-codes/*")
public class SwiftCodeDetailsServlet extends HttpServlet {
    private Datasource datasource;

    @Override
    public void init() throws ServletException {
        this.datasource = (Datasource) getServletContext().getAttribute("database");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            String swiftCode = path.substring(1);
            SwiftRecord swiftRecord = datasource.getCodeRepository().findSwiftRecordBySwiftCode(swiftCode);
            if (swiftRecord == null) {
                send(response, Map.of("message", "SWIFT Record not found"));
            } else {
                if(swiftRecord.isHeadquarter()){
                    List<SwiftRecord> branches = datasource.getCodeRepository().findAllBranchesRecordsByHeadquarter(swiftRecord.getSwiftCode());
                    send(response, SwiftMapper.mapHeadquarterSwiftRecord(swiftRecord, branches));
                } else {
                    send(response, SwiftMapper.mapSingleBranchSwiftRecord(swiftRecord));
                }
            }
        } catch (Exception ex){
            send(response, Map.of("message", "internal server error"));
            System.err.println("Error handling request: " + ex.getMessage());
        }
    }

    // Helper method for sending serialized data
    public void send(HttpServletResponse response, Object message) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonMessage = gson.toJson(message);
        response.getWriter().write(jsonMessage);
    }
}
