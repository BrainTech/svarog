package org.signalml.plugin.newstager.logic.book;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.signalml.plugin.newstager.data.NewStagerBookData;
import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.book.NewStagerBookSample;

public class NewStagerBookDataProvider {

	private CountDownLatch event;
	private AtomicBoolean shutdownFlag;

	private NewStagerBookData bookData;

	private AtomicInteger position;

	public NewStagerBookDataProvider() {
		this.event = new CountDownLatch(1);
		this.position = new AtomicInteger();
		this.shutdownFlag = new AtomicBoolean(false);

		this.bookData = null;
	}


	public void setBookData(NewStagerBookData bookData) {
		this.bookData = bookData;
		this.event.countDown();
	}

	public NewStagerBookInfo getBookInfo() throws InterruptedException {
		this.event.await();
		if (this.shutdownFlag.get()) {
			return null;
		}

		return this.bookData.bookInfo;
	}

	public NewStagerBookSample getNextBookAtoms() throws InterruptedException {
		this.event.await();
		if (this.shutdownFlag.get()) {
			return null;
		}

		int current = this.position.getAndIncrement();
		if (current >= this.bookData.atoms.length) {
			return null;
		}
		return new NewStagerBookSample(current, this.bookData.atoms[current], this.bookData.bookInfo);
	}

	public void shutdown() {
		this.shutdownFlag.set(true);
		this.event.countDown();
	}

}
