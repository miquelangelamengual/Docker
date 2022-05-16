package cat.iesmanacor.gestibgsuite.manager;

import cat.iesmanacor.gestibgsuite.model.gestib.Curs;
import cat.iesmanacor.gestibgsuite.model.gestib.Grup;
import cat.iesmanacor.gestibgsuite.model.gestib.Usuari;
import cat.iesmanacor.gestibgsuite.model.google.Dispositiu;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreu;
import cat.iesmanacor.gestibgsuite.model.google.GrupCorreuTipus;
import cat.iesmanacor.gestibgsuite.repository.gestib.CursRepository;
import cat.iesmanacor.gestibgsuite.repository.gestib.UsuariRepository;
import cat.iesmanacor.gestibgsuite.repository.google.GrupCorreuRepository;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class GoogleSpreadsheetService {
    private final static String[] DOMAINS = {"iesmanacor.cat", "alumnes.iesmanacor.cat"};
    private final static String DOMAIN = "iesmanacor.cat";
    @Value("${gc.keyfile}")
    private String keyFile;
    @Value("${gc.adminUser}")
    private String adminUser;
    @Value("${centre.usuaris.passwordinicial}")
    private String passwordInicial;

    @Value("${gc.nomprojecte}")
    private String nomProjecte;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private CursRepository cursRepository;

    @Autowired
    private GrupCorreuRepository grupCorreuRepository;


    public void helloSpreadsheets() throws IOException, GeneralSecurityException {
        String[] scopes = {SheetsScopes.SPREADSHEETS};

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(this.adminUser);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle("hola que tal"));
        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());


        List<List<Object>> values = Arrays.asList(
                Arrays.asList("Grup de correu", "adg21a@iesmanacor.cat", "Data llistat", "27/10/2021"),
                Arrays.asList(""),
                Arrays.asList("ORDRE", "Primer llinatge", "Segon Llinatge", "Nom", "Correu Corporatiu"),
                Arrays.asList("ORDRE", "Primer llinatge", "Segon Llinatge", "Nom", "Correu Corporatiu"),
                Arrays.asList("ORDRE", "Primer llinatge", "Segon Llinatge", "Nom", "Correu Corporatiu"),
                Arrays.asList("ORDRE", "Primer llinatge", "Segon Llinatge", "Nom", "Correu Corporatiu"),
                Arrays.asList("ORDRE", "Primer llinatge", "Segon Llinatge", "Nom", "Correu Corporatiu"),
                Arrays.asList("ORDRE", "Primer llinatge", "Segon Llinatge", "Nom", "Correu Corporatiu"),
                Arrays.asList("ORDRE", "Primer llinatge", "Segon Llinatge", "Nom", "Correu Corporatiu"),
                Arrays.asList("ORDRE", "Primer llinatge", "Segon Llinatge", "Nom", "Correu Corporatiu"),
                Arrays.asList("ORDRE", "Primer llinatge", "Segon Llinatge", "Nom", "Correu Corporatiu")
        );
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result =
                service.spreadsheets().values().update(spreadsheet.getSpreadsheetId(), "A1:Z1000", body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
        System.out.printf("%d cells updated.", result.getUpdatedCells());
    }

    public List<List<String>> getSpreadsheetDataTable(String id, String myEmail) throws IOException, GeneralSecurityException, MessagingException {
        String[] scopes = {SheetsScopes.SPREADSHEETS};

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(myEmail);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        ValueRange valueRange = service.spreadsheets().values().get(id, "A1:Z1000").execute();
        List<List<Object>> values = valueRange.getValues();

        List<List<String>> linies = new ArrayList<>();

        for (List<Object> value : values) {
            List<String> linia = new ArrayList<>();

            for (Object valor : value) {
                linia.add(valor.toString());
            }
            linies.add(linia);
        }

        return linies;
    }

    public Spreadsheet alumnesGrup(List<Grup> grups, String myEmail) throws IOException, GeneralSecurityException {
        //List<Grup> grups = grupRepository.findAll();
        grups.sort((g1, g2) -> {
            Curs c1 = cursRepository.findCursByGestibIdentificador(g1.getGestibCurs());
            Curs c2 = cursRepository.findCursByGestibIdentificador(g2.getGestibCurs());

            String str1 = c1.getGestibNom() + g1.getGestibNom();
            String str2 = c2.getGestibNom() + g2.getGestibNom();

            return str1.compareTo(str2);
        });

        LocalDate localDate = LocalDate.now();
        String today = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String[] scopes = {SheetsScopes.SPREADSHEETS};

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(myEmail);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        List<Sheet> sheets = new ArrayList<>();

        for (Grup g : grups) {
            Curs c = cursRepository.findCursByGestibIdentificador(g.getGestibCurs());
            List<GrupCorreu> grupsCorreu = grupCorreuRepository.findAllByGestibGrup(g.getGestibIdentificador());
            String grupsCorreuStr = grupsCorreu.stream().filter(gc -> gc.getGrupCorreuTipus().equals(GrupCorreuTipus.ALUMNAT)).map(gc -> gc.getGsuiteEmail()).collect(Collectors.joining(", "));


            List<Usuari> alumnes = usuariRepository.findAllByGestibGrupOrGestibGrup2OrGestibGrup3(g.getGestibIdentificador(), g.getGestibIdentificador(), g.getGestibIdentificador());
            alumnes.sort((a1, a2) -> {
                String str1 = a1.getGestibCognom1() + " " + a1.getGestibCognom2() + ", " + a1.getGestibNom();
                String str2 = a2.getGestibCognom1() + " " + a2.getGestibCognom2() + ", " + a2.getGestibNom();

                return str1.compareTo(str2);
            });
            Sheet s = new Sheet();

            //Propietats de la pàgina
            SheetProperties sproperties = new SheetProperties();
            sproperties.setTitle(c.getGestibNom() + " " + g.getGestibNom());
            s.setProperties(sproperties);

            //Dades de la pàgina
            List<RowData> rows = new ArrayList<>();

            //Row 1
            List<CellData> cellsRow1 = new ArrayList<>();
            cellsRow1.add(writeCell("GRUP DE CORREU", true));
            cellsRow1.add(writeCell(grupsCorreuStr));
            cellsRow1.add(writeCell("Data llistat", true));
            cellsRow1.add(writeCell(today));

            RowData row1 = new RowData();
            row1.setValues(cellsRow1);
            rows.add(row1);

            //Row 2
            List<CellData> cellsRow2 = new ArrayList<>();
            cellsRow2.add(writeCell(""));

            RowData row2 = new RowData();
            row2.setValues(cellsRow2);
            rows.add(row2);

            //Row 3
            List<CellData> cellsRow3 = new ArrayList<>();
            cellsRow3.add(writeCell("ORDRE", true));
            cellsRow3.add(writeCell("Primer llinatge", true));
            cellsRow3.add(writeCell("Segon Llinatge", true));
            cellsRow3.add(writeCell("Nom", true));
            cellsRow3.add(writeCell("Correu Corporatiu", true));

            RowData row3 = new RowData();
            row3.setValues(cellsRow3);
            rows.add(row3);

            //Row alumnes
            int i = 1;
            for (Usuari alumne : alumnes) {
                List<CellData> cellsRowAlumne = new ArrayList<>();
                cellsRowAlumne.add(writeCell(String.valueOf(i++)));
                cellsRowAlumne.add(writeCell(alumne.getGestibCognom1()));
                cellsRowAlumne.add(writeCell(alumne.getGestibCognom2()));
                cellsRowAlumne.add(writeCell(alumne.getGestibNom()));
                cellsRowAlumne.add(writeCell(alumne.getGsuiteEmail()));

                RowData rowAlumne = new RowData();
                rowAlumne.setValues(cellsRowAlumne);
                rows.add(rowAlumne);
            }


            List<GridData> dades = new ArrayList<>();
            GridData d = new GridData();
            d.setStartColumn(0);
            d.setStartRow(0);
            d.setRowData(rows);
            dades.add(d);

            s.setData(dades);

            sheets.add(s);
        }


        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle("Alumnat per grup - " + today));

        spreadsheet.setSheets(sheets);

        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId,spreadsheetUrl,properties")
                .execute();

        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        System.out.println("Spreadsheet URL: " + spreadsheet.getSpreadsheetUrl());
        System.out.println("Spreadsheet Title: " + spreadsheet.getProperties().getTitle());
        return spreadsheet;
    }

    public Spreadsheet usuarisGrupCorreu(@NotNull List<GrupCorreu> grupsCorreu, String myEmail) throws IOException, GeneralSecurityException {
        //List<Grup> grups = grupRepository.findAll();
        grupsCorreu.sort((g1, g2) -> {
            String str1 = g1.getGsuiteEmail();
            String str2 = g2.getGsuiteEmail();

            return str1.compareTo(str2);
        });

        LocalDate localDate = LocalDate.now();
        String today = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String[] scopes = {SheetsScopes.SPREADSHEETS};

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(myEmail);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        List<Sheet> sheets = new ArrayList<>();

        for (GrupCorreu g : grupsCorreu) {
            List<Usuari> usuaris = new ArrayList<>(g.getUsuaris());
            usuaris.sort((a1, a2) -> {
                String str1 = a1.getGestibCognom1() + " " + a1.getGestibCognom2() + ", " + a1.getGestibNom();
                String str2 = a2.getGestibCognom1() + " " + a2.getGestibCognom2() + ", " + a2.getGestibNom();

                return str1.compareTo(str2);
            });
            Sheet s = new Sheet();

            //Propietats de la pàgina
            SheetProperties sproperties = new SheetProperties();
            sproperties.setTitle(g.getGsuiteEmail());
            s.setProperties(sproperties);

            //Dades de la pàgina
            List<RowData> rows = new ArrayList<>();

            //Row 1
            List<CellData> cellsRow1 = new ArrayList<>();
            cellsRow1.add(writeCell("GRUP DE CORREU", true));
            cellsRow1.add(writeCell(g.getGsuiteEmail()));
            cellsRow1.add(writeCell("Data llistat", true));
            cellsRow1.add(writeCell(today));

            RowData row1 = new RowData();
            row1.setValues(cellsRow1);
            rows.add(row1);

            //Row 2
            List<CellData> cellsRow2 = new ArrayList<>();
            cellsRow2.add(writeCell(""));

            RowData row2 = new RowData();
            row2.setValues(cellsRow2);
            rows.add(row2);

            //Row 3
            List<CellData> cellsRow3 = new ArrayList<>();
            cellsRow3.add(writeCell("ORDRE", true));
            cellsRow3.add(writeCell("Primer llinatge", true));
            cellsRow3.add(writeCell("Segon Llinatge", true));
            cellsRow3.add(writeCell("Nom", true));
            cellsRow3.add(writeCell("Correu Corporatiu", true));

            RowData row3 = new RowData();
            row3.setValues(cellsRow3);
            rows.add(row3);

            //Row alumnes
            int i = 1;
            for (Usuari usuari : usuaris) {
                List<CellData> cellsRowAlumne = new ArrayList<>();
                cellsRowAlumne.add(writeCell(String.valueOf(i++)));
                cellsRowAlumne.add(writeCell(usuari.getGestibCognom1()));
                cellsRowAlumne.add(writeCell(usuari.getGestibCognom2()));
                cellsRowAlumne.add(writeCell(usuari.getGestibNom()));
                cellsRowAlumne.add(writeCell(usuari.getGsuiteEmail()));

                RowData rowAlumne = new RowData();
                rowAlumne.setValues(cellsRowAlumne);
                rows.add(rowAlumne);
            }


            List<GridData> dades = new ArrayList<>();
            GridData d = new GridData();
            d.setStartColumn(0);
            d.setStartRow(0);
            d.setRowData(rows);
            dades.add(d);

            s.setData(dades);

            sheets.add(s);
        }


        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle("Usuaris per grup de correu - " + today));

        spreadsheet.setSheets(sheets);

        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId,spreadsheetUrl,properties")
                .execute();

        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        System.out.println("Spreadsheet URL: " + spreadsheet.getSpreadsheetUrl());
        System.out.println("Spreadsheet Title: " + spreadsheet.getProperties().getTitle());
        return spreadsheet;
    }

    public Spreadsheet usuarisDispositiu(List<Dispositiu> dispositiusAssignats, List<Dispositiu> dispositiusNoAssignats, List<Usuari> usuarisAmbChromebook, List<Usuari> usuarisSenseChromebook, String myEmail) throws IOException, GeneralSecurityException {

        LocalDate localDate = LocalDate.now();
        String today = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String[] scopes = {SheetsScopes.SPREADSHEETS};

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(myEmail);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        List<Sheet> sheets = new ArrayList<>();

        //DISPOSITIUS ASSIGNATS (Alumnes i professors i altres)
        Sheet sheetDispositiusAssignatsAlumnes = new Sheet();
        Sheet sheetDispositiusAssignatsProfessors = new Sheet();
        Sheet sheetDispositiusAssignatsAltres = new Sheet();

        //Propietats de la fulla
        SheetProperties spropertiesDispositiusAssignatsAlumnes = new SheetProperties();
        spropertiesDispositiusAssignatsAlumnes.setTitle("Dispositius assignats alumnes");
        sheetDispositiusAssignatsAlumnes.setProperties(spropertiesDispositiusAssignatsAlumnes);

        SheetProperties spropertiesDispositiusAssignatsProfessors = new SheetProperties();
        spropertiesDispositiusAssignatsProfessors.setTitle("Dispositius assignats professors");
        sheetDispositiusAssignatsProfessors.setProperties(spropertiesDispositiusAssignatsProfessors);

        SheetProperties spropertiesDispositiusAssignatsAltres = new SheetProperties();
        spropertiesDispositiusAssignatsAltres.setTitle("Dispositius assignats altres");
        sheetDispositiusAssignatsAltres.setProperties(spropertiesDispositiusAssignatsAltres);

        //Dades de la pàgina
        List<RowData> rowsDispositiusAssignatsAlumnes = new ArrayList<>();
        List<RowData> rowsDispositiusAssignatsProfessors = new ArrayList<>();
        List<RowData> rowsDispositiusAssignatsAltres = new ArrayList<>();

        //Row 1
        List<CellData> cellsRow1DispositiusAssignats = new ArrayList<>();
        cellsRow1DispositiusAssignats.add(writeCell("ID Dispositiu", true));
        cellsRow1DispositiusAssignats.add(writeCell("Estat", true));
        cellsRow1DispositiusAssignats.add(writeCell("Model", true));
        cellsRow1DispositiusAssignats.add(writeCell("Número de sèrie", true));
        cellsRow1DispositiusAssignats.add(writeCell("MAC", true));
        cellsRow1DispositiusAssignats.add(writeCell("Unitat Organitzativa", true));
        cellsRow1DispositiusAssignats.add(writeCell("Usuari", true));
        cellsRow1DispositiusAssignats.add(writeCell("Data llistat", true));
        cellsRow1DispositiusAssignats.add(writeCell(today));

        RowData row1DispositiusAssignats = new RowData();
        row1DispositiusAssignats.setValues(cellsRow1DispositiusAssignats);
        rowsDispositiusAssignatsAlumnes.add(row1DispositiusAssignats);
        rowsDispositiusAssignatsProfessors.add(row1DispositiusAssignats);
        rowsDispositiusAssignatsAltres.add(row1DispositiusAssignats);


        //Row dispositius
        for (Dispositiu dispositiu : dispositiusAssignats) {
            List<CellData> cellsRowDispositiu = new ArrayList<>();
            cellsRowDispositiu.add(writeCell(dispositiu.getIdDispositiu()));
            cellsRowDispositiu.add(writeCell(dispositiu.getEstat()));
            cellsRowDispositiu.add(writeCell(dispositiu.getModel()));
            cellsRowDispositiu.add(writeCell(dispositiu.getSerialNumber()));
            cellsRowDispositiu.add(writeCell(dispositiu.getMacAddress()));
            cellsRowDispositiu.add(writeCell(dispositiu.getOrgUnitPath()));
            cellsRowDispositiu.add(writeCell(dispositiu.getUsuari().getGsuiteFullName()));

            RowData rowDispositiu = new RowData();
            rowDispositiu.setValues(cellsRowDispositiu);

            if(dispositiu.getUsuari().getGestibProfessor() != null && dispositiu.getUsuari().getGestibProfessor()){
                rowsDispositiusAssignatsProfessors.add(rowDispositiu);
            } else if(dispositiu.getUsuari().getGestibAlumne() != null && dispositiu.getUsuari().getGestibAlumne()) {
                rowsDispositiusAssignatsAlumnes.add(rowDispositiu);
            } else {
                rowsDispositiusAssignatsAltres.add(rowDispositiu);
            }
        }

        //Configurem grid data d'alumnes
        List<GridData> dadesDispositiusAlumnes = new ArrayList<>();
        GridData dDispositiusAlumnes = new GridData();
        dDispositiusAlumnes.setStartColumn(0);
        dDispositiusAlumnes.setStartRow(0);
        dDispositiusAlumnes.setRowData(rowsDispositiusAssignatsAlumnes);
        dadesDispositiusAlumnes.add(dDispositiusAlumnes);

        sheetDispositiusAssignatsAlumnes.setData(dadesDispositiusAlumnes);


        //Configurem Grid Data de professors
        List<GridData> dadesDispositiusProfessors = new ArrayList<>();
        GridData dDispositiusProfessors = new GridData();
        dDispositiusProfessors.setStartColumn(0);
        dDispositiusProfessors.setStartRow(0);
        dDispositiusProfessors.setRowData(rowsDispositiusAssignatsProfessors);
        dadesDispositiusProfessors.add(dDispositiusProfessors);

        sheetDispositiusAssignatsProfessors.setData(dadesDispositiusProfessors);

        //Configurem Grid Data de altres
        List<GridData> dadesDispositiusAltres = new ArrayList<>();
        GridData dDispositiusAltres = new GridData();
        dDispositiusAltres.setStartColumn(0);
        dDispositiusAltres.setStartRow(0);
        dDispositiusAltres.setRowData(rowsDispositiusAssignatsAltres);
        dadesDispositiusAltres.add(dDispositiusAltres);

        sheetDispositiusAssignatsAltres.setData(dadesDispositiusAltres);

        sheets.add(sheetDispositiusAssignatsAlumnes);
        sheets.add(sheetDispositiusAssignatsProfessors);
        sheets.add(sheetDispositiusAssignatsAltres);


        //DISPOSITIUS NO ASSIGNATS
        Sheet sheetDispositiusNoAssignats = new Sheet();

        //Propietats de la fulla
        SheetProperties spropertiesDispositiusNoAssignats = new SheetProperties();
        spropertiesDispositiusNoAssignats.setTitle("Dispositius no assignats");
        sheetDispositiusNoAssignats.setProperties(spropertiesDispositiusNoAssignats);

        List<RowData> rowsDispositiusNoAssignats = new ArrayList<>();

        //Row 1
        List<CellData> cellsRow1DispositiusNoAssignats = new ArrayList<>();
        cellsRow1DispositiusNoAssignats.add(writeCell("ID Dispositiu", true));
        cellsRow1DispositiusNoAssignats.add(writeCell("Estat", true));
        cellsRow1DispositiusNoAssignats.add(writeCell("Model", true));
        cellsRow1DispositiusNoAssignats.add(writeCell("Número de sèrie", true));
        cellsRow1DispositiusNoAssignats.add(writeCell("MAC", true));
        cellsRow1DispositiusNoAssignats.add(writeCell("Unitat Organitzativa", true));
        cellsRow1DispositiusNoAssignats.add(writeCell("Data llistat", true));
        cellsRow1DispositiusNoAssignats.add(writeCell(today));

        RowData row1DispositiusNoAssignats = new RowData();
        row1DispositiusNoAssignats.setValues(cellsRow1DispositiusNoAssignats);
        rowsDispositiusNoAssignats.add(row1DispositiusNoAssignats);

        for (Dispositiu dispositiu : dispositiusNoAssignats) {
            List<CellData> cellsRowDispositiu = new ArrayList<>();
            cellsRowDispositiu.add(writeCell(dispositiu.getIdDispositiu()));
            cellsRowDispositiu.add(writeCell(dispositiu.getEstat()));
            cellsRowDispositiu.add(writeCell(dispositiu.getModel()));
            cellsRowDispositiu.add(writeCell(dispositiu.getSerialNumber()));
            cellsRowDispositiu.add(writeCell(dispositiu.getMacAddress()));
            cellsRowDispositiu.add(writeCell(dispositiu.getOrgUnitPath()));

            RowData rowDispositiu = new RowData();
            rowDispositiu.setValues(cellsRowDispositiu);

            rowsDispositiusNoAssignats.add(rowDispositiu);
        }

        //Configurem Grid Data de altres
        List<GridData> dadesDispositiusNoAssignats = new ArrayList<>();
        GridData dDispositiusNoAssignats = new GridData();
        dDispositiusNoAssignats.setStartColumn(0);
        dDispositiusNoAssignats.setStartRow(0);
        dDispositiusNoAssignats.setRowData(rowsDispositiusNoAssignats);
        dadesDispositiusNoAssignats.add(dDispositiusNoAssignats);

        sheetDispositiusNoAssignats.setData(dadesDispositiusNoAssignats);

        sheets.add(sheetDispositiusNoAssignats);


        //USUARIS SENSE CHROMEBOOK (Alumnes, Professors i Altres)
        Sheet sheetUsuarisSenseDispositiusAssignatsAlumnes = new Sheet();
        Sheet sheetUsuarisSenseDispositiusAssignatsProfessors = new Sheet();
        Sheet sheetUsuarisSenseDispositiusAssignatsAltres = new Sheet();

        //Propietats de la fulla
        SheetProperties spropertiesUsuarisSenseDispositiusAssignatsAlumnes = new SheetProperties();
        spropertiesUsuarisSenseDispositiusAssignatsAlumnes.setTitle("Alumnes sense Dispositius assignats");
        sheetUsuarisSenseDispositiusAssignatsAlumnes.setProperties(spropertiesUsuarisSenseDispositiusAssignatsAlumnes);

        SheetProperties spropertiesUsuarisSenseDispositiusAssignatsProfessors = new SheetProperties();
        spropertiesUsuarisSenseDispositiusAssignatsProfessors.setTitle("Professors sense dispositius assignats");
        sheetUsuarisSenseDispositiusAssignatsProfessors.setProperties(spropertiesUsuarisSenseDispositiusAssignatsProfessors);

        SheetProperties spropertiesUsuarisSenseDispositiusAssignatsAltres = new SheetProperties();
        spropertiesUsuarisSenseDispositiusAssignatsAltres.setTitle("Altres usuaris sense dispositius assignats");
        sheetUsuarisSenseDispositiusAssignatsAltres.setProperties(spropertiesUsuarisSenseDispositiusAssignatsAltres);

        //Dades de la pàgina
        List<RowData> rowsUsuarisSenseDispositiusAssignatsAlumnes = new ArrayList<>();
        List<RowData> rowsUsuarisSenseDispositiusAssignatsProfessors = new ArrayList<>();
        List<RowData> rowsUsuarisSenseDispositiusAssignatsAltres = new ArrayList<>();

        //Row 1
        List<CellData> cellsRow1UsuarisSenseDispositiusAssignats = new ArrayList<>();
        cellsRow1UsuarisSenseDispositiusAssignats.add(writeCell("Cognoms", true));
        cellsRow1UsuarisSenseDispositiusAssignats.add(writeCell("Nom", true));
        cellsRow1UsuarisSenseDispositiusAssignats.add(writeCell("Unitat Organitzativa", true));
        cellsRow1UsuarisSenseDispositiusAssignats.add(writeCell("Email", true));
        cellsRow1UsuarisSenseDispositiusAssignats.add(writeCell("Data llistat", true));
        cellsRow1UsuarisSenseDispositiusAssignats.add(writeCell(today));

        RowData row1UsuarisSenseDispositiusAssignats = new RowData();
        row1UsuarisSenseDispositiusAssignats.setValues(cellsRow1UsuarisSenseDispositiusAssignats);
        rowsUsuarisSenseDispositiusAssignatsAlumnes.add(row1UsuarisSenseDispositiusAssignats);
        rowsUsuarisSenseDispositiusAssignatsProfessors.add(row1UsuarisSenseDispositiusAssignats);
        rowsUsuarisSenseDispositiusAssignatsAltres.add(row1UsuarisSenseDispositiusAssignats);


        //Row dispositius
        for (Usuari usuari : usuarisSenseChromebook) {
            List<CellData> cellsRowUsuarisSenseDispositiu = new ArrayList<>();
            cellsRowUsuarisSenseDispositiu.add(writeCell(usuari.getGsuiteFamilyName()));
            cellsRowUsuarisSenseDispositiu.add(writeCell(usuari.getGsuiteGivenName()));
            cellsRowUsuarisSenseDispositiu.add(writeCell(usuari.getGsuiteUnitatOrganitzativa()));
            cellsRowUsuarisSenseDispositiu.add(writeCell(usuari.getGsuiteEmail()));

            RowData rowUsuarisSenseDispositiu = new RowData();
            rowUsuarisSenseDispositiu.setValues(cellsRowUsuarisSenseDispositiu);

            if(usuari.getGestibProfessor() != null && usuari.getGestibProfessor()){
                rowsUsuarisSenseDispositiusAssignatsProfessors.add(rowUsuarisSenseDispositiu);
            } else if(usuari.getGestibAlumne() != null && usuari.getGestibAlumne()) {
                rowsUsuarisSenseDispositiusAssignatsAlumnes.add(rowUsuarisSenseDispositiu);
            } else {
                rowsUsuarisSenseDispositiusAssignatsAltres.add(rowUsuarisSenseDispositiu);
            }
        }

        //Configurem grid data d'alumnes
        List<GridData> dadesUsuarisSenseDispositiusAlumnes = new ArrayList<>();
        GridData dUsuarisSenseDispositiusAlumnes = new GridData();
        dUsuarisSenseDispositiusAlumnes.setStartColumn(0);
        dUsuarisSenseDispositiusAlumnes.setStartRow(0);
        dUsuarisSenseDispositiusAlumnes.setRowData(rowsUsuarisSenseDispositiusAssignatsAlumnes);
        dadesUsuarisSenseDispositiusAlumnes.add(dUsuarisSenseDispositiusAlumnes);

        sheetUsuarisSenseDispositiusAssignatsAlumnes.setData(dadesUsuarisSenseDispositiusAlumnes);


        //Configurem Grid Data de professors
        List<GridData> dadesUsuarisSenseDispositiusProfessors = new ArrayList<>();
        GridData dUsuarisSenseDispositiusProfessors = new GridData();
        dUsuarisSenseDispositiusProfessors.setStartColumn(0);
        dUsuarisSenseDispositiusProfessors.setStartRow(0);
        dUsuarisSenseDispositiusProfessors.setRowData(rowsUsuarisSenseDispositiusAssignatsProfessors);
        dadesUsuarisSenseDispositiusProfessors.add(dUsuarisSenseDispositiusProfessors);

        sheetUsuarisSenseDispositiusAssignatsProfessors.setData(dadesUsuarisSenseDispositiusProfessors);

        //Configurem Grid Data de altres
        List<GridData> dadesUsuarisSenseDispositiusAltres = new ArrayList<>();
        GridData dUsuarisSenseDispositiusAltres = new GridData();
        dUsuarisSenseDispositiusAltres.setStartColumn(0);
        dUsuarisSenseDispositiusAltres.setStartRow(0);
        dUsuarisSenseDispositiusAltres.setRowData(rowsUsuarisSenseDispositiusAssignatsAltres);
        dadesUsuarisSenseDispositiusAltres.add(dUsuarisSenseDispositiusAltres);

        sheetUsuarisSenseDispositiusAssignatsAltres.setData(dadesUsuarisSenseDispositiusAltres);

        sheets.add(sheetUsuarisSenseDispositiusAssignatsAlumnes);
        sheets.add(sheetUsuarisSenseDispositiusAssignatsProfessors);
        sheets.add(sheetUsuarisSenseDispositiusAssignatsAltres);

        Spreadsheet spreadsheet = new Spreadsheet().setProperties(new SpreadsheetProperties().setTitle("Llistat Chromebooks amb Usuaris - " + today));

        spreadsheet.setSheets(sheets);

        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId,spreadsheetUrl,properties")
                .execute();

        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        System.out.println("Spreadsheet URL: " + spreadsheet.getSpreadsheetUrl());
        System.out.println("Spreadsheet Title: " + spreadsheet.getProperties().getTitle());
        return spreadsheet;
    }

    public Spreadsheet usuarisCustom(List<Usuari> usuaris, String myEmail) throws IOException, GeneralSecurityException {

        System.out.println("Usuu"+usuaris.get(0).getIdusuari());
        LocalDate localDate = LocalDate.now();
        String today = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String[] scopes = {SheetsScopes.SPREADSHEETS};

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(this.keyFile)).createScoped(scopes).createDelegated(myEmail);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), requestInitializer).setApplicationName(this.nomProjecte).build();

        List<Sheet> sheets = new ArrayList<>();

        //DISPOSITIUS ASSIGNATS (Alumnes i professors i altres)
        Sheet sheetAlumnes = new Sheet();
        Sheet sheetProfessors = new Sheet();
        Sheet sheetAltres = new Sheet();

        //Propietats de la fulla
        SheetProperties spropertiesAlumnes = new SheetProperties();
        spropertiesAlumnes.setTitle("Alumnes");
        sheetAlumnes.setProperties(spropertiesAlumnes);

        SheetProperties spropertiesProfessors = new SheetProperties();
        spropertiesProfessors.setTitle("Professors");
        sheetProfessors.setProperties(spropertiesProfessors);

        SheetProperties spropertiesAltres = new SheetProperties();
        spropertiesAltres.setTitle("Altres");
        sheetAltres.setProperties(spropertiesAltres);

        //Dades de la pàgina
        List<RowData> rowsAlumnes = new ArrayList<>();
        List<RowData> rowsProfessors = new ArrayList<>();
        List<RowData> rowsAltres = new ArrayList<>();

        //Row 1
        List<CellData> cellsRow1 = new ArrayList<>();
        cellsRow1.add(writeCell("Cognom1", true));
        cellsRow1.add(writeCell("Cognom2", true));
        cellsRow1.add(writeCell("Nom", true));
        cellsRow1.add(writeCell("Correu electrònic", true));
        cellsRow1.add(writeCell("Unitat Organitzativa", true));
        cellsRow1.add(writeCell("Grups Correu", true));

        List<CellData> cellsRow1Alumnes = new ArrayList<>(cellsRow1);
        cellsRow1Alumnes.add(writeCell("Unitat Organitzativa alumneesss", true));


        RowData row1Alumnes = new RowData();
        row1Alumnes.setValues(cellsRow1Alumnes);
        rowsAlumnes.add(row1Alumnes);

        RowData row1 = new RowData();
        row1.setValues(cellsRow1);
        rowsProfessors.add(row1);
        rowsAltres.add(row1);


        //Row usuaris
        for (Usuari usuari : usuaris) {
            List<CellData> cellsRow = new ArrayList<>();
            cellsRow.add(writeCell(usuari.getGestibCognom1()));
            cellsRow.add(writeCell(usuari.getGestibCognom2()));
            cellsRow.add(writeCell(usuari.getGestibNom()));
            cellsRow.add(writeCell(""));
            cellsRow.add(writeCell(""));
            cellsRow.add(writeCell(""));
            cellsRow.add(writeCell(""));

            RowData row = new RowData();
            row.setValues(cellsRow);

            if(usuari.getGestibProfessor() != null && usuari.getGestibProfessor()){
                rowsProfessors.add(row);
            } else if(usuari.getGestibAlumne() != null && usuari.getGestibAlumne()) {
                rowsAlumnes.add(row);
            } else {
                rowsAltres.add(row);
            }
        }

        //Configurem grid data d'alumnes
        List<GridData> dadesAlumnes = new ArrayList<>();
        GridData dAlumnes = new GridData();
        dAlumnes.setStartColumn(0);
        dAlumnes.setStartRow(0);
        dAlumnes.setRowData(rowsAlumnes);
        dadesAlumnes.add(dAlumnes);

        sheetAlumnes.setData(dadesAlumnes);


        //Configurem Grid Data de professors
        List<GridData> dadesProfessors = new ArrayList<>();
        GridData dProfessors = new GridData();
        dProfessors.setStartColumn(0);
        dProfessors.setStartRow(0);
        dProfessors.setRowData(rowsProfessors);
        dadesProfessors.add(dProfessors);

        sheetProfessors.setData(dadesProfessors);

        //Configurem Grid Data de altres
        List<GridData> dadesAltres = new ArrayList<>();
        GridData dAltres = new GridData();
        dAltres.setStartColumn(0);
        dAltres.setStartRow(0);
        dAltres.setRowData(rowsAltres);
        dadesAltres.add(dAltres);

        sheetAltres.setData(dadesAltres);

        sheets.add(sheetAlumnes);
        sheets.add(sheetProfessors);
        sheets.add(sheetAltres);


        Spreadsheet spreadsheet = new Spreadsheet().setProperties(new SpreadsheetProperties().setTitle("Llistat Usuaris Personalitzat - " + today));

        spreadsheet.setSheets(sheets);

        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId,spreadsheetUrl,properties")
                .execute();

        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        System.out.println("Spreadsheet URL: " + spreadsheet.getSpreadsheetUrl());
        System.out.println("Spreadsheet Title: " + spreadsheet.getProperties().getTitle());
        return spreadsheet;
    }

    private CellData writeCell(String valor) {
        return writeCell(valor, false);
    }

    private CellData writeCell(String valor, boolean negreta) {
        TextFormat tf = new TextFormat();
        tf.setBold(negreta);

        List<TextFormatRun> textFormatRuns = new ArrayList<>();
        TextFormatRun textFormatRun = new TextFormatRun();
        textFormatRun.setFormat(tf);
        textFormatRun.setStartIndex(0);
        textFormatRuns.add(textFormatRun);

        CellFormat cellFormat = new CellFormat();
        cellFormat.setTextFormat(tf);

        CellData cellData = new CellData();

        ExtendedValue ev = new ExtendedValue();
        ev.setStringValue(valor);
        cellData.setUserEnteredValue(ev);

        if (valor != null && !valor.isEmpty()) {
            cellData.setEffectiveFormat(cellFormat);
            cellData.setTextFormatRuns(textFormatRuns);
        }
        return cellData;
    }
}

