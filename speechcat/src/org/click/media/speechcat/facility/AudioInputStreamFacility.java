package org.click.media.speechcat.facility;

import java.io.File;
import java.net.URL;

/**
 * facility for reading one wav file maybe we can refer sphinx
 * 
 * @author blue
 */
public class AudioInputStreamFacility {

	/**
	 * we scan the wav file two passes 1) we get the length of data 2) we read
	 * data into the array[]
	 * 
	 * @param wavFile
	 * @return
	 */
	public double[] readRawWavFile(String wavFile) {

		try {
		
			AudioFileDataSource afd = new AudioFileDataSource(3200);
			File testFile = new File(wavFile);
			URL audioURL = testFile.toURI().toURL();

			// first pass we get data length
			afd.setAudioFile(audioURL, null);
			while (!(afd.streamEndReached)) {
				afd.getData();
			}

			// second pass we read data into array
			int dataLength = afd.allReadLength;
			afd.mallocDataArray(dataLength);

			afd.setAudioFile(audioURL, null);
			afd.readDataPass = true;
			while (!(afd.streamEndReached)) {
				afd.getData();
			}

			double[] localReadDataArray = afd.readDataArray;

			for (int i = 0; i < 100; i++) {
				System.err.print(localReadDataArray[i] + " ");
			}

			System.err.println();

			for (int i = localReadDataArray.length-100; i < localReadDataArray.length; i++) {
				System.err.print(localReadDataArray[i] + " ");
			}

			System.err.println();
			
			return localReadDataArray;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {

		try {
			// File testFile = new
			// File("rnn_exmpales/phoneme_transcription/data/SA2.WAV");
			// File testFile = new
			// File("rnn_exmpales/phoneme_transcription/data/SI1728.WAV");
			// File testFile = new
			// File("rnn_exmpales/phoneme_transcription/data/SI648.WAV");
			// File testFile = new
			// File("rnn_exmpales/phoneme_transcription/data/SX225.WAV");
			// File testFile = new
			// File("rnn_exmpales/phoneme_transcription/data/SX62.WAV");
			// File testFile = new
			// File("rnn_exmpales/phoneme_transcription/data/SX70.WAV");

			// File testFile = new File("doc/10001-90210-01803.wav");
			// URL audioURL =
			// Transcriber.class.getResource("dr1_fcjf0_sx397.wav");
			AudioInputStreamFacility aisf=new AudioInputStreamFacility();

			String[] testFileNames = { "SA2.WAV", "SI1728.WAV", "SI648.WAV", "SX225.WAV", "SX62.WAV", "SX70.WAV" };
			String testFileName = "";

			AudioFileDataSource afd = new AudioFileDataSource(3200);
			for (int i = 0; i < 2; i++) {
				testFileName = testFileNames[i];
				double[] readall=aisf.readRawWavFile("rnn_exmpales/phoneme_transcription/data/" + testFileName);
				System.err.println("readall.len:"+readall.length);
				/*
				File testFile = new File("rnn_exmpales/phoneme_transcription/data/" + testFileName);
				URL audioURL = testFile.toURI().toURL();

				afd.setAudioFile(audioURL, null);

				while (!(afd.streamEndReached)) {
					afd.getData();
				}

				System.err.println("testFileName:" + testFileName + " allReadLength:" + afd.allReadLength);
                */
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
