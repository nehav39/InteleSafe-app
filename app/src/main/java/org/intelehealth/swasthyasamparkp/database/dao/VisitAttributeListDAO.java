package org.intelehealth.swasthyasamparkp.database.dao;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.intelehealth.swasthyasamparkp.app.AppConstants;
import org.intelehealth.swasthyasamparkp.models.dto.VisitAttributeDTO;
import org.intelehealth.swasthyasamparkp.utilities.exception.DAOException;

import java.util.List;
import java.util.UUID;

/**
 * Created By: Prajwal Waingankar on 01-Jun-21
 * Github: prajwalmw
 * Email: prajwalwaingankar@gmail.com
 */

public class VisitAttributeListDAO {
    private long createdRecordsCount = 0;

    public boolean insertProvidersAttributeList(List<VisitAttributeDTO> visitAttributeDTOS)
            throws DAOException {

        boolean isInserted = true;
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        try {
            for (VisitAttributeDTO visitDTO : visitAttributeDTOS) {
                createVisitAttributeList(visitDTO, db);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            isInserted = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return isInserted;
    }


    private boolean createVisitAttributeList(VisitAttributeDTO visitDTO, SQLiteDatabase db) throws DAOException {

        boolean isCreated = true;
        ContentValues values = new ContentValues();
        String where = "visit_uuid=?";
        String whereArgs[] = {visitDTO.getVisit_uuid()};
        try {

//            values.put("speciality_value", visitDTO.getValue());
            values.put("uuid", visitDTO.getUuid());
            values.put("visit_uuid", visitDTO.getVisit_uuid());
            values.put("value", visitDTO.getValue());
            values.put("visit_attribute_type_uuid", visitDTO.getVisit_attribute_type_uuid());
            values.put("voided", visitDTO.getVoided());
            values.put("sync", "1");

            if (visitDTO.getVisit_attribute_type_uuid().equalsIgnoreCase("3f296939-c6d3-4d2e-b8ca-d7f4bfd42c2d")) {
                createdRecordsCount = db.insertWithOnConflict("tbl_visit_attribute", null, values, SQLiteDatabase.CONFLICT_REPLACE);

                if (createdRecordsCount != -1) {
                    Log.d("SPECI", "SIZEVISTATTR: " + createdRecordsCount);
                } else {
                    Log.d("SPECI", "SIZEVISTATTR: " + createdRecordsCount);
                }
            }
        } catch (SQLException e) {
            isCreated = false;
            throw new DAOException(e.getMessage(), e);
        } finally {

        }

        return isCreated;
    }

   /**
    * @param visitUuid : The visit uuid of the created visit is passed.
    * @param speciality_selected : The speciality value is passed here bydefault: General Physician.
    * @return isInserted : returns @true if the speciality was inserted in the db.
    */
    public boolean insertVisitAttributes(String visitUuid, String speciality_selected) throws
            DAOException {
        boolean isInserted = false;

        Log.d("SPINNER", "SPINNER_Selected_visituuid_logs: " + visitUuid);
        Log.d("SPINNER", "SPINNER_Selected_value_logs: " + speciality_selected);

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        try {
            values.put("uuid", UUID.randomUUID().toString()); //as per patient attributes uuid generation.
            values.put("visit_uuid", visitUuid);
            values.put("value", speciality_selected);
            values.put("visit_attribute_type_uuid", "3f296939-c6d3-4d2e-b8ca-d7f4bfd42c2d");
            values.put("voided", "0");
            values.put("sync", "0");

            long count = db.insertWithOnConflict("tbl_visit_attribute", null,
                    values, SQLiteDatabase.CONFLICT_REPLACE);

            if (count != -1)
                isInserted = true;

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            isInserted = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

        Log.d("isInserted", "isInserted: " + isInserted);
        return isInserted;
    }

}
