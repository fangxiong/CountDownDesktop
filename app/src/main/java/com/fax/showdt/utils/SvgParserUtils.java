package com.fax.showdt.utils;

import android.content.res.AssetManager;
import android.graphics.Path;

import androidx.core.graphics.PathParser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-19
 * Description:
 */
public class SvgParserUtils {

    public List<Data> parseSvg(){
        File file = new File("file:///android_asset/svg/icon1.svg");
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(file);
            String xpathExpression = "//@*"; // 选择所有节点
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expression = xpath.compile(xpathExpression);
            // 得到整个xml的每个节点
            NodeList svgPaths = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
            ArrayList<Data> pathList = new ArrayList<>();
            for (int i = 0; i < svgPaths.getLength(); i++) {
                // 对应path标签中的d节点
                if (svgPaths.item(i).getNodeName().equals("d")) {
                    Data data = new Data();
                    data.mPath = PathParser.createPathFromPathData(svgPaths.item(i).getTextContent());
                    // 此d节点前面应该是fill节点，fill节点是此路径绘制的颜色
                    if (i > 0 && svgPaths.item(i - 1).getNodeName().equals("fill")) {
                        data.mFillColor = svgPaths.item(i - 1).getTextContent();
                    } else if (i > 0){
                        // d节点的前一个不是fill节点，可能是其他节点，此时往上查找，直到找到fill节点或者再次
                        // 遇到d节点(证明没有fill节点，默认为黑色)为止
                        for (int j = i -1; j >= 0 ; j--) {
                            if (svgPaths.item(j).getNodeName().equals("fill")) {
                                data.mFillColor = svgPaths.item(j).getTextContent();
                                break;
                            } else if (svgPaths.item(j).getNodeName().equals("d")) {
                                // 如果找到了上一个path中的d标签都没有找到fill标签，则代表此path没有fill，为黑色
                                data.mFillColor = "#000000";
                                break;
                            }
                        }
                    }
                    pathList.add(data);
                }
            }
            return pathList;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    class Data {
        String mFillColor;
        Path mPath;
    }
}
