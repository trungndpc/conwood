package vn.conwood.admin.controller.importer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.conwood.admin.util.City;
import vn.conwood.common.Permission;
import vn.conwood.jpa.entity.UserEntity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomerExcelImporter {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final CustomerExcelImporter INSTANCE = new CustomerExcelImporter();

    public List<UserEntity> read(InputStream inputStream) throws Exception {
        List<UserEntity> users = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();
        iterator.next();
        while (iterator.hasNext()) {
            UserEntity userEntity = new UserEntity();
            Row currentRow = iterator.next();
            Cell phoneCell = currentRow.getCell(0);
            if (phoneCell != null) {
                userEntity = getPhone(phoneCell, userEntity);
                if (userEntity != null) {
                    userEntity = getInseeCode(currentRow.getCell(1), userEntity);
                    userEntity = getName(currentRow.getCell(2), userEntity);
                    userEntity = getCity(currentRow.getCell(3), userEntity);
                    userEntity = getNameCompany(currentRow.getCell(4), userEntity);
                    userEntity = getPosition(currentRow.getCell(5), userEntity);
                    userEntity = getRoleId(currentRow.getCell(6), userEntity);
                    users.add(userEntity);
                }
            }
        }
        workbook.close();
        return users;
    }

    private UserEntity getPhone(Cell cell, UserEntity userEntity) throws Exception {
        try{
            String phone = "";
            switch (cell.getCellType()) {
                case STRING:
                    phone = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    phone = "0" + Double.toString(cell.getNumericCellValue());
                    break;
            }
            if (!phone.startsWith("0")) {
                throw new Exception("phone is valid");
            }
            userEntity.setPhone(phone);
        }catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
        return userEntity;
    }

    private UserEntity getCity(Cell cell, UserEntity userEntity) throws Exception {
        String strCity = cell.getStringCellValue();
        strCity = strCity.replaceAll("Tỉnh", "");
        strCity = strCity.replaceAll("Thành phố", "");
        strCity = strCity.trim();
        int cityId = City.findByString(strCity);
        if (cityId == 0) {
            throw new Exception("city is 0");
        }
        userEntity.setCityId(cityId);
        return userEntity;
    }

    private UserEntity getName(Cell cell, UserEntity userEntity) {
        userEntity.setName(cell.getStringCellValue());
        return userEntity;
    }

    private UserEntity getInseeCode(Cell cell, UserEntity userEntity) {
        String code = cell.getStringCellValue();
        if (code.startsWith("CONWOOD")) {
            userEntity.setInseeId(code);
        }
        return userEntity;
    }

    private UserEntity getNameCompany(Cell cell, UserEntity userEntity) {
        userEntity.setNameCompany(cell.getStringCellValue());
        return userEntity;
    }

    private UserEntity getPosition(Cell cell, UserEntity userEntity) {
        userEntity.setPosition(cell.getStringCellValue());
        return userEntity;
    }

    private UserEntity getRoleId(Cell cell, UserEntity userEntity) throws Exception {
        String strRoleId = cell.getStringCellValue();
        if (Permission.CONSTRUCTOR.getName().equals(strRoleId)) {
            userEntity.setRoleId(Permission.CONSTRUCTOR.getId());
            return userEntity;
        }

        if (Permission.ARCHITECTURE.getName().equals(strRoleId)) {
            userEntity.setRoleId(Permission.ARCHITECTURE.getId());
            return userEntity;
        }
        if (userEntity.getRoleId() == null) {
            throw new Exception("roleId: " + strRoleId + " is invalid");
        }
        return userEntity;
    }
}
