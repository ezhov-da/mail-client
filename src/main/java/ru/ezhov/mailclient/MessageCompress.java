package ru.ezhov.mailclient;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Упаковка архива
 * <p>
 *
 * @author ezhov_da
 */
class MessageCompress {
    static final int BUFFER = 2048;
    private static final Logger LOG = Logger.getLogger(MessageCompress.class.getName());
    private List<FileMessageSave> fileMessageSaves;

    MessageCompress(List<FileMessageSave> fileMessageSaves) {
        this.fileMessageSaves = fileMessageSaves;
    }

    public List<FileMessageSave> compress() {
        fileMessageSaves.forEach((FileMessageSave fms) ->
        {
            compressMassageAndCheckError(fms);
        });
        return fileMessageSaves;
    }

    private void compressMassageAndCheckError(FileMessageSave fileMessageSave) {
        //получаем файл с расширением zip
        File file = new File(fileMessageSave.getFullFilePathWithExte(FileMessageSave.Ext.ZIP));
        byte data[] = new byte[BUFFER];
        ZipOutputStream zipOutputStream = null;
        try {
            //пакуем файл
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)), Charset.forName("cp866"));
            FileInputStream fi = new FileInputStream(file);
            try (BufferedInputStream origin = new BufferedInputStream(fi)) {
                ZipEntry entry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    zipOutputStream.write(data, 0, count);
                }

                fileMessageSave.setNowExt(FileMessageSave.Ext.ZIP);
            } catch (IOException ex) {
                fileMessageSave.setError(true);
                fileMessageSave.setException(ex);
                LOG.log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            fileMessageSave.setError(true);
            fileMessageSave.setException(ex);
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
