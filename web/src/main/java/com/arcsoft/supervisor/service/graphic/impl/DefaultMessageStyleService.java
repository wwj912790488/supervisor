package com.arcsoft.supervisor.service.graphic.impl;

import com.arcsoft.supervisor.model.domain.graphic.MessageStyle;
import com.arcsoft.supervisor.repository.graphic.MessageStyleRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.graphic.MessageStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultMessageStyleService implements MessageStyleService, TransactionSupport{

    @Autowired
    private MessageStyleRepository messageStyleRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public MessageStyle getDefault() {
        MessageStyle style = messageStyleRepository.findOne(1);
        if(style == null) {
            style = new MessageStyle();
            style.setId(1);
            style.setFont("宋体");
            style.setSize(42);
            style.setColor(0xffffff);
            style.setAlpha(100);
            style.setX(0);
            style.setY(0);
            style.setWidth(500);
            style.setHeight(50);
            style = messageStyleRepository.save(style);
        }
        return style;
    }

    @Override
    public void updateDefault(MessageStyle style) {
        MessageStyle defaultStyle = getDefault();
        defaultStyle.setFont(style.getFont());
        defaultStyle.setSize(style.getSize());
        defaultStyle.setColor(style.getColor());
        defaultStyle.setAlpha(style.getAlpha());
        defaultStyle.setX(style.getX());
        defaultStyle.setY(style.getY());
        defaultStyle.setWidth(style.getWidth());
        defaultStyle.setHeight(style.getHeight());
    }

    @Override
    public MessageStyle save(MessageStyle style) {
        return messageStyleRepository.save(style);
    }
}
