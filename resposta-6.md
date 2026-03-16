```java
package com.shipay.challenge.bot;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class UserExportBot {

    public static void main(String[] args) throws Exception {
        greetings();
        
        System.out.println("Press Ctrl+C to exit");

        // Loop infinito, o ideal seria uma tarefa agendada (@Scheduled).
        while (true) {
            try {
                // As credenciais não podem estar Hardcoded no código.
                // Elas precisam vir de variável de ambiente.
                Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/bot_db", "postgres", "123mudar");
                
                Statement stmt = conn.createStatement();

                // Select sem paginação ou limit pode dar OutOfMemory e derrubar o sistema.
                ResultSet rs = stmt.executeQuery("SELECT * FROM users;");

                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String fileName = "data_export_" + timeStamp + ".xlsx";
                
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Users");
                
                int index = 0;
                Row header = sheet.createRow(index);
                header.createCell(0).setCellValue("Id");
                header.createCell(1).setCellValue("Name");
                header.createCell(2).setCellValue("Email");

                // Exportar senhas de usuários para um Excel é uma falha grave de segurança 
                header.createCell(3).setCellValue("Password");

                header.createCell(4).setCellValue("Role Id");
                header.createCell(5).setCellValue("Created At");
                
                while (rs.next()) {
                    index++;
                    Row row = sheet.createRow(index);

                    // Ao invés de utilizar o número da coluna, o ideal seria utilizar o nome.
                    // Pois assim fica mais fácil de ler o código e não corre o risco de salvar dados errados
                    // caso a ordem das colunas mude no banco de dados.
                    System.out.println("Id: " + rs.getLong(1));
                    row.createCell(0).setCellValue(rs.getLong(1));
                    
                    System.out.println("Name: " + rs.getString(2));
                    row.createCell(1).setCellValue(rs.getString(2));
                    
                    System.out.println("Email: " + rs.getString(3));
                    row.createCell(2).setCellValue(rs.getString(3));

                    //Não pode printar senhas de usuários no log do servidor
                    System.out.println("Password: " + rs.getString(4));
                    row.createCell(3).setCellValue(rs.getString(4));
                    
                    row.createCell(4).setCellValue(rs.getLong(5));
                    row.createCell(5).setCellValue(rs.getString(6));
                }
                
                FileOutputStream fileOut = new FileOutputStream(fileName);
                workbook.write(fileOut);

                // O fechamento das conexões e arquivos deve ser feito em um bloco 'finally'.
                // Pois se der erro antes, as conexões ficarão abertas e poderá exceder o limite de pool.
                fileOut.close();
                workbook.close();
                conn.close();

                //Não é uma boa prática usar System.out para logs. O correto é usar SLF4J/Logback
                System.out.println("job executed!");

                // Se houver exceção no código acima, o sleep é ignorado. 
                // E assim, O while(true) fará um loop infinito sem pausa, causando 100% de CPU.
                // O sleep deveria ficar no 'finally'.
                Thread.sleep(60000); 

            } catch (Exception e) {
                // Imprimir a stack trace dessa forma não é uma boa prática porque não imprime a data/hora. 
                // O ideal é utilizar um Logger como o SLF4J para imprimir a exceção com contexto do erro.
                // Exemplo: log.error("Falha critica ao tentar exportar a base de usuarios.", e);
                e.printStackTrace();
            }
        }
    }

    public static void greetings() {
        System.out.println("             ##########################");
        System.out.println("             # - ACME - Tasks Robot - #");
        System.out.println("             # - v 1.0 - 2020-07-28 - #");
        System.out.println("             ##########################");
    }
}
```
