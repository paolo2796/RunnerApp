package it.unisa.runnerapp.beans;

import java.util.Date;

public class LiveRequest
{
    private Runner sender;
    private Date   sendDate;

    public LiveRequest()
    {
    }

    public LiveRequest(Runner sender,Date sendDate)
    {
        this.sender=sender;
        this.sendDate=sendDate;
    }

    public void setSender(Runner sender)
    {
        this.sender=sender;
    }

    public Runner getSender()
    {
        return sender;
    }

    public void setSendingDate(Date sendDate)
    {
        this.sendDate=sendDate;
    }

    public Date getSendingDate()
    {
        return sendDate;
    }
}
