package com.yihen.core.model;

import com.yihen.YihenDramaApplication;
import com.yihen.controller.vo.TextModelRequestVO;
import com.yihen.enums.SceneCode;
import com.yihen.service.StoryboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = YihenDramaApplication.class,
        properties = {
                "app.websocket.enabled=false",
                "spring.main.lazy-initialization=true",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        })
public class ShotGenerateTextModelServiceTest {

    @Autowired
    private ShotGenerateTextModelService shotGenerateTextModelService;

    @Test
    public void test() throws Exception {
        TextModelRequestVO textModelRequestVO = new TextModelRequestVO();
        textModelRequestVO.setText("暑假的日头刚爬过教学楼顶，金灿灿的光线透过香樟树叶，在 C 市第一中学门口的柏油路上筛下细碎的光斑，踩上去像踏着一地碎金。\n" +
                "　　暑假本该沉寂的校园因高三生补课多了点人气，校门口反而格外清静，只有几辆送完学生的电动车慢悠悠驶离，连卖早点的小摊都收了摊，只剩蝉鸣此起彼伏地撞在树叶上，倒衬得周遭更静了。\n" +
                "　　林砚拽着宋伊棠的手腕，掌心带着点薄汗，却握得很稳。他脚步急促得带起一阵风，眉头微蹙，眼神牢牢锁定校园深处。\n" +
                "　　宋伊棠被他拉着快步走，米白色的裙摆扫过路面的光斑，鬓角的碎发被风吹得贴在脸颊，脸上满是紧张又藏不住兴奋的神色：“能追上吗？”\n" +
                "　　“应该还在高三楼附近。” 林砚低头看了她一眼，声音压得低，脚步没半分停顿。\n" +
                "　　两人像在追逐一缕看不见的风，径直朝着校门快步穿行。\n" +
                "　　迎面走来的中年男人拎着公文包，本是匆匆赶路，瞥见这对气质格外的年轻男女，下意识侧头多看了一眼。\n" +
                "　　还没看清两人眉眼间的模样，就见他们身影轻轻一晃，几乎是贴着他的胳膊掠了过去。男人忍不住回头，想看看他们要往哪儿去。\n" +
                "　　可身后空荡荡的。刚才还并肩快步走的两人，像被阳光悄悄收了去，连半点影子都没留下，只有香樟树叶被风吹得沙沙响，柏油路上的光斑依旧晃眼，仿佛那对匆匆而过的男女，只是他眼花看错的一道虚影。\n" +
                "　　男人愣了愣，挠了挠头，目光在空荡荡的身后扫了一圈，没再找到半个人影，只好嘟囔着 “怪事”，转身走了。\n" +
                "　　而他回头的同一瞬间，高三教学楼东侧的香樟树荫里，两道浅光骤然凝聚。\n" +
                "　　林砚松开宋伊棠的手，指尖还带着赶路的薄汗：“没被盯太久。”\n" +
                "　　宋伊棠扶着树干喘了口气，指尖沾了点树干的凉意，抬头望了眼三楼高三A班的窗户，眼底闪着亮：“跑不掉了吧？刚才那股气息，明明就在这儿。”\n" +
                "　　两人身影微微一晃，如同融入树荫的光斑，悄无声息地穿过教学楼的墙壁，隐进了校园深处。两人脚步立刻放轻，借着林荫掩护，沿教学楼外墙悄摸摸到楼梯口。\n" +
                "　　踮着脚尖上行，全程压低声响，最终隐进走廊深处。\n" +
                "　　到高三A班后门，讲课声、粉笔摩擦黑板的声音，混着窗外蝉鸣传来。\n" +
                "　　林砚和宋伊棠贴墙往里望，瞬间锁定教室前排，一团半透明灰雾黏在低头记笔记的男生头顶，阴冷气息萦绕不散，正一点点往他天灵盖里钻。\n" +
                "　　男生笔尖猛地顿住，抬手揉了揉太阳穴，眉头蹙起，眼神有些涣散，原本工整的笔记上，多了一道歪扭的墨痕。他晃了晃脑袋，想驱散眩晕，却没察觉头顶灰雾愈发浓稠，边缘还泛起了黑丝。\n" +
                "　　“要阻止他。”宋伊棠声音压得极低，指尖凝起一缕浅金色微光。\n" +
                "　　林砚点头，指尖泛起淡青色气流。\n" +
                "　　趁老师转身写板书、全班低头记笔记的间隙，两人指尖光芒骤然亮起，两道光束顺着门缝射进教室，精准击中灰雾。\n" +
                "　　一声极细微的“滋啦”声响起，灰雾猛地一颤，从男生头顶弹起，轮廓瞬间扭曲，还发出尖锐嘶鸣。林砚立刻拉着宋伊棠缩进走廊阴影，借着墙壁藏好身形。\n" +
                "　　灰雾显然被激怒，扭曲的轮廓转向两人方向，隐约有两点红光。但它忌惮两人气息，也怕被普通人察觉，没敢上前，猛地转身冲向教室后方窗户。\n");
        textModelRequestVO.setModelId(3L);
        textModelRequestVO.setSceneCode(SceneCode.STORYBOARD_GEN);
        textModelRequestVO.setProjectId(3L);

        shotGenerateTextModelService.extract(textModelRequestVO);
    }
}
