import MenuHeader from "../util-components/MenuHeader/MenuHeader";
import "./AboutPage.css"

function AboutPage(){
    return(
        <div>
            <MenuHeader/>
            <div className="about-page-body">
                <h1 className="about-page-hero">Package Health Insights for Modern Software Development</h1>
                <p className="about-page-subheading">Platypulse analyzes open-source packages across ecosystems and transforms hundreds of maintenance, 
                    adoption, activity, and risk signals into a single health score.</p>
            </div>
            <div>
                
            </div>
        </div>
    );
}

export default AboutPage;