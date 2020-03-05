import React from 'react';
import { NavLink } from 'react-router-dom';
import qdlogo from '../../assets/navbar/qdlogo.png';
import './navBar.scss';
import dcc_logo from '../../assets/navbar/qf_white.svg';

/**
 * Side navigation bar component.
 */
export default class NavBar extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dmvDisabled: true,
            emrDisabled: true,
        }
        this.handleDmvClick = this.handleDmvClick.bind(this);
        this.handleEmrClick = this.handleEmrClick.bind(this);
    }

    render() {
        return (
            <div className="sidebar"> 
                <NavLink to='/'><div className="brand">
                    <img src={qdlogo} alt="logo" className="logo"/>
                    <img src={dcc_logo} alt="logo" className="dcclogo"/>
                </div></NavLink>
                <ul className="appMenu">
                    <li className='menu-nav'><i className="material-icons">attach_money</i><span>EMR</span><i className="material-icons">keyboard_arrow_down</i></li>
                    <li><NavLink className='menu-nav submenu' to='/emrHealth'><span>Observability</span></NavLink></li>
                    <li><NavLink className='menu-nav submenu' to='/emrManagement'><span>Orchestration</span></NavLink></li>
                    <li><NavLink className='menu-nav submenu' to='/emrCost'><span>Cost</span></NavLink></li>
                    {this.props.superAdmin ? <li><NavLink className='menu-nav' to='/admin'><i className="material-icons">people</i><span>Administration</span></NavLink></li> : null}
                    
                </ul>
                <ul className="userMenu">
                    <li><NavLink className='menu-nav' to='/profile'><i className='material-icons'>account_circle</i><span>{this.props.fullName}</span></NavLink></li>
                    <li><NavLink className='menu-nav' to='/help'><i className="material-icons">help_outline</i><span>Help</span></NavLink></li>
                </ul>
            </div>
        )
    }

    handleDmvClick() {
        let val = this.state.dmvDisabled;
        this.setState({
            ...this.state,
            dmvDisabled: !val
        })
    }

    handleEmrClick() {
        let val = this.state.emrDisabled;
        this.setState({
            ...this.state,
            emrDisabled: !val
        });
    }
}